package com.vantage.bulls.service;

import com.vantage.bulls.dao.OptionChainDAO;
import com.vantage.bulls.dto.OptionChainResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class OptionChainSchedulerImpl implements OptionChainScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionChainSchedulerImpl.class);
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    @Value("${market.start.time:09:15:00}")
    private String startTimeStr;

    @Value("${market.end.time:15:30:00}")
    private String endTimeStr;

    private LocalTime marketStartTime;
    private LocalTime marketEndTime;

    @PostConstruct
    public void init() {
        // Parse once at startup
        this.marketStartTime = LocalTime.parse(startTimeStr);
        this.marketEndTime = LocalTime.parse(endTimeStr);
    }

    @Autowired
    private OptionChainService ocService;

    @Autowired
    private OptionChainDAO ocDAOService;

    @Override
    @Scheduled(fixedDelay = 4000)
    public void processOptionChain() {

        LocalTime now = LocalTime.now(IST);

        // 1. Wait until exactly 9:15:00 AM IST
        if (now.isBefore(marketStartTime)) {
            LOGGER.info("Current time is {}, hence, waiting for {} AM IST for market to start...", now, marketStartTime);
            return;
        }

        // 2. Stop exactly at 3:30:00 PM IST (15:30)
        if (now.isAfter(marketEndTime)) {
            LOGGER.info("Fetching OC data stopped at {} as currently time is {} and market is closed. Shutting down application.",marketEndTime, now);
            System.exit(0);
            return;
        }

        this.fetchAndSaveData();

    }

    private void fetchAndSaveData() {
        LocalTime now = LocalTime.now(IST);
        try {
            LOGGER.info("Fetching OC data at {} IST.", now);
            OptionChainResponse optionChain = ocService.getOptionChain();
            LOGGER.info("OC data fetch successfully at {}", LocalTime.now(IST));

            LOGGER.info("Saving data into database at {}", LocalTime.now(IST));
            ocDAOService.saveOptionChain(optionChain);
            LOGGER.info("Data saved successfully at {}", LocalTime.now(IST));

        } catch (Exception e) {
            LOGGER.error("Error during OC fetch and save process: ", e);
        }
    }
}

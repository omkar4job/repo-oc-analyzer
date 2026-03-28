package com.vantage.bulls.service;

import com.vantage.bulls.dao.OptionChainDAO;
import com.vantage.bulls.dto.OptionChainResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class OptionChainSchedulerImpl implements OptionChainScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionChainSchedulerImpl.class);
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    @Autowired
    private OptionChainService ocService;

    @Autowired
    private OptionChainDAO ocDAOService;

    @Override
    // Corrected 6-field cron: Seconds, Minutes, Hours, DayOfMonth, Month, DayOfWeek
    //@Scheduled(cron = "0/4 * 9-15 * * MON-FRI", zone = "Asia/Kolkata")
    @Scheduled(fixedDelay = 4000)
    public void processOptionChain() {

        LocalTime now = LocalTime.now(IST);

        // 1. Wait until exactly 9:15:00 AM IST
        //if (now.isBefore(LocalTime.of(9, 15, 0))) {
        if (now.isBefore(LocalTime.of(0, 15, 0))) {
            LOGGER.info("Current time is {}, waiting for 9:15:00 AM IST...", now);
            return;
        }

        // 2. Stop exactly at 3:30:00 PM IST (15:30)
        if (now.isAfter(LocalTime.of(15, 30, 0))) {
            LOGGER.info("Fetching OC data stopped at 3:30:00 PM IST. Shutting down application.");
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

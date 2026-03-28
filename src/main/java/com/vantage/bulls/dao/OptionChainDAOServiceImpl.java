package com.vantage.bulls.dao;

import com.vantage.bulls.dto.OptionChainResponse;
import com.vantage.bulls.dto.OptionStrikeData;
import com.vantage.bulls.dto.ResponseData;
import com.vantage.bulls.model.OptionChainRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("optionChainDAOService")
public class OptionChainDAOServiceImpl implements OptionChainDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionChainDAOServiceImpl.class);

    @Autowired
    private NiftyOptionChainRepository optionChainRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final LocalTime MARKET_OPEN_THRESHOLD = LocalTime.of(0, 0, 0);

    @Override
    @Transactional
    public boolean saveOptionChain(OptionChainResponse optionChain) {
        LocalDateTime now = LocalDateTime.now();
        ResponseData ocData = optionChain.getData();
        Map<String, OptionStrikeData> oc = ocData.getOc();


        List<OptionChainRecordDTO> optionChainRecordDTOS = oc.entrySet().parallelStream().map(entry -> {
            String strikePrice = entry.getKey();
            OptionStrikeData data = entry.getValue();
            OptionChainRecordDTO dto = new OptionChainRecordDTO();
            // Metadata
            dto.setTimestamp(now);
            dto.setSpotPrice(ocData.getLastPrice() != null ? ocData.getLastPrice().floatValue() : 0f);
            dto.setStrikePrice(strikePrice);

            // Map Call Options (CE)
            if (data.ce != null) {
                dto.setCeLTP(data.ce.lastPrice != null ? data.ce.lastPrice.floatValue() : 0f);
                dto.setCeVolume(data.ce.volume);
                dto.setCeOi(data.ce.oi);
                //dto.setCeOiChange(data.ce.oi - data.ce.previousOi); // Calculating change
                dto.setCeImpliedVolatility(data.ce.impliedVolatility != null ? data.ce.impliedVolatility.floatValue() : 0f);
                dto.setCePrevOi(data.ce.previousOi);
                dto.setCePrevClosePrice(data.ce.previousClosePrice != null ? data.ce.previousClosePrice.floatValue() : 0f);
                dto.setCePrevVolume(data.ce.previousVolume);
                dto.setCeAvgPrice(data.ce.averagePrice != null ? data.ce.averagePrice.floatValue() : 0f);

                if (data.ce.greeks != null) {
                    dto.setCeDelta(data.ce.greeks.delta != null ? data.ce.greeks.delta.floatValue() : 0f);
                    dto.setCeTheta(data.ce.greeks.theta != null ? data.ce.greeks.theta.floatValue() : 0f);
                    dto.setCeGamma(data.ce.greeks.gamma != null ? data.ce.greeks.gamma.floatValue() : 0f);
                    dto.setCeVega(data.ce.greeks.vega != null ? data.ce.greeks.vega.floatValue() : 0f);
                }
            }

            // Map Put Options (PE)
            if (data.pe != null) {
                dto.setPeLTP(data.pe.lastPrice != null ? data.pe.lastPrice.floatValue() : 0f);
                dto.setPeVolume(data.pe.volume);
                dto.setPeOi(data.pe.oi);
                //dto.setPeOiChange(data.pe.oi - data.pe.previousOi);
                dto.setPeImpliedVolatility(data.pe.impliedVolatility != null ? data.pe.impliedVolatility.floatValue() : 0f);
                dto.setPePrevOi(data.pe.previousOi);
                dto.setPePrevClosePrice(data.pe.previousClosePrice != null ? data.pe.previousClosePrice.floatValue() : 0f);
                dto.setPePrevVolume(data.pe.previousVolume);
                dto.setPeAvgPrice(data.pe.averagePrice != null ? data.pe.averagePrice.floatValue() : 0f);

                if (data.pe.greeks != null) {
                    dto.setPeDelta(data.pe.greeks.delta != null ? data.pe.greeks.delta.floatValue() : 0f);
                    dto.setPeTheta(data.pe.greeks.theta != null ? data.pe.greeks.theta.floatValue() : 0f);
                    dto.setPeGamma(data.pe.greeks.gamma != null ? data.pe.greeks.gamma.floatValue() : 0f);
                    dto.setPeVega(data.pe.greeks.vega != null ? data.pe.greeks.vega.floatValue() : 0f);
                }
            }

            // Calculate Ratios and Differences
            if (data.ce != null && data.pe != null) {
                long ceOi = data.ce.oi != null ? data.ce.oi : 0L;
                long peOi = data.pe.oi != null ? data.pe.oi : 0L;
                dto.setPcOiDiff(peOi - ceOi);
                // PCR calculation (avoiding division by zero)
                if (ceOi > 0) {
                    dto.setPcr((double)(peOi / ceOi));
                } else {
                    dto.setPcr(0.0d);
                }
            }

            dto.setCeVolumeChange(0L);
            dto.setPeVolumeChange(0L);
            dto.setPcVolumeDiff(0L);
            dto.setPcVolumeChangeDiff(0L);

            return dto;
        }).filter(dto -> dto.getCeAvgPrice() != 0.0f && dto.getPeAvgPrice() != 0.0f)
                .collect(Collectors.toList());

        this.optionChainRepository.saveAll(optionChainRecordDTOS);

        // Trigger the async process
        this.eventPublisher.publishEvent(new OCBatchSavedEvent(now));
        LOGGER.info("Main Thread: Batch {} saved. Event published.", now);
        return true;
    }

    /**
     * Phase 2: Runs in background after Phase 1 commits.
     * Calculates the differences between this batch and the previous one.
     */
    @Async("spExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processCalculations(OCBatchSavedEvent event) {
        LocalDateTime currentTs = event.timestamp(); // Record getter syntax

        // 1. Fetch current batch of records
        List<OptionChainRecordDTO> currentBatch = this.optionChainRepository.findByTimestamp(currentTs);

        // 2. Identify the previous batch timestamp
        Optional<LocalDateTime> prevTsOpt = this.optionChainRepository.findTopTimestampBefore(currentTs);

        if (prevTsOpt.isPresent()) {
            LocalDateTime prevTs = prevTsOpt.get();

            // 3. Fetch previous 94 and map by strikePrice for O(1) lookup speed
            Map<String, OptionChainRecordDTO> prevMap = this.optionChainRepository.findByTimestamp(prevTs)
                    .stream()
                    .collect(Collectors.toMap(OptionChainRecordDTO::getStrikePrice, r -> r));

            // 4. Calculate the changes (OI Change, etc.)
            for (OptionChainRecordDTO current : currentBatch) {
                OptionChainRecordDTO previous = prevMap.get(current.getStrikePrice());
                if (previous != null) {
                    performCalculations(current, previous);
                }
            }

            // 5. Update the current batch with the new values
            this.optionChainRepository.saveAll(currentBatch);
            LOGGER.info("Background: Calculations complete for batch {}. Compared against {}.", currentTs, prevTs);
        } else {
            LOGGER.info("Background: Initial batch detected for {}. No previous data to compare.", currentTs);
        }
    }

    private void performCalculations(OptionChainRecordDTO curr, OptionChainRecordDTO prev) {
        LocalTime currentTime = curr.getTimestamp().toLocalTime();
        LOGGER.info("Comparing data of oi data at current timestamp {} with prev timestamp of {}", curr.getTimestamp(), prev.getTimestamp());

        // CE Calculations
        long ceOiChange = 0L;
        if (curr.getCeOi() != null && prev.getCeOi() != null) {
            if (currentTime.isBefore(MARKET_OPEN_THRESHOLD)) {
                // Logic: currentOI - previousDayChange
                long prevDayOI = (curr.getCePrevOi() != null) ? curr.getCePrevOi() : 0L;
                ceOiChange = curr.getCeOi() - prevDayOI;

            } else {
                // Logic: currentOI - previousOI
                ceOiChange = curr.getCeOi() - prev.getCeOi();
            }
            curr.setCeOiChange(ceOiChange);
        }
        long ceVolumeChange = 0L;
        if (curr.getCeVolume() != null && prev.getCeVolume() != null) {
            if (currentTime.isBefore(MARKET_OPEN_THRESHOLD)) {
                long prevDayVolume = (curr.getCePrevVolume() != null) ? curr.getCePrevVolume() : 0L;
                ceVolumeChange = curr.getCeVolume() - prevDayVolume;
            } else {
                ceVolumeChange = curr.getCeVolume() - prev.getCeVolume();
            }
            curr.setCeVolumeChange(ceVolumeChange);
        }

        // PE Calculations
        long peOiChange = 0L;
        if (curr.getPeOi() != null && prev.getPeOi() != null) {
            if (currentTime.isBefore(MARKET_OPEN_THRESHOLD)) {
                // Logic: currentOI - previousChange
                long prevDayOI = (curr.getPePrevOi() != null) ? curr.getPePrevOi() : 0L;
                peOiChange = curr.getPeOi() - prevDayOI;
            } else {
                // Logic: currentOI - previousOI
                peOiChange = curr.getPeOi() - prev.getPeOi();
            }
            curr.setPeOiChange(peOiChange);
        }
        long peVolumeChange = 0L;
        if (curr.getPeVolume() != null && prev.getPeVolume() != null) {
            if (currentTime.isBefore(MARKET_OPEN_THRESHOLD)) {
                long prevDayVolume = (curr.getPePrevVolume() != null) ? curr.getPePrevVolume() : 0L;
                peVolumeChange = curr.getPeVolume() - prevDayVolume;
            } else {
                peVolumeChange = curr.getPeVolume() - prev.getPeVolume();
            }
            curr.setPeVolumeChange(peVolumeChange);
        }

        long putCallOIChangeDifference = curr.getPeOiChange() - curr.getCeOiChange();
        curr.setPcOiChangeDiff(putCallOIChangeDifference);

        long putCallVolumeDifference = curr.getPeVolume() - curr.getCeVolume();
        curr.setPcVolumeDiff(putCallVolumeDifference);

        long putCallVolumeChangeDifference = curr.getPeVolumeChange() - curr.getCeVolumeChange();
        curr.setPcVolumeChangeDiff(putCallVolumeChangeDifference);

        LOGGER.info("ceOiChange = {}", ceOiChange);
        LOGGER.info("ceVolumeChange = {}", ceVolumeChange);
        LOGGER.info("peOiChange = {}", peOiChange);
        LOGGER.info("peVolumeChange = {}", peVolumeChange);
        LOGGER.info("putCallOIChangeDifference = {}", putCallOIChangeDifference);
        LOGGER.info("putCallVolumeDifference = {}", putCallVolumeDifference);
        LOGGER.info("putCallVolumeChangeDifference = {}\n\n", putCallVolumeChangeDifference);
        LOGGER.info("Finished computing data for OI at timestamp {} with prev data for OI at timestamp {}", curr.getTimestamp(), prev.getTimestamp());

    }

}

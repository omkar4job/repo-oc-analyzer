package com.vantage.bulls.dao;

import com.vantage.bulls.dto.OptionChainResponse;
import com.vantage.bulls.dto.OptionStrikeData;
import com.vantage.bulls.dto.ResponseData;
import com.vantage.bulls.model.OptionChainRecordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("optionChainDAOService")
public class OptionChainDAOServiceImpl implements OptionChainDAO {

    @Autowired
    private NiftyOptionChainRepository optionChainRepository;

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
                dto.setCeOiChange(data.ce.oi - data.ce.previousOi); // Calculating change
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
                dto.setPeOiChange(data.pe.oi - data.pe.previousOi);
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
                    dto.setPcr((int) (peOi / ceOi));
                } else {
                    dto.setPcr(0);
                }
            }

            return dto;
        }).collect(Collectors.toList());

        optionChainRecordDTOS.parallelStream().forEach(record -> {
            this.optionChainRepository.save(record);
        });

        this.optionChainRepository.saveAll(optionChainRecordDTOS);

        return true;
    }



}

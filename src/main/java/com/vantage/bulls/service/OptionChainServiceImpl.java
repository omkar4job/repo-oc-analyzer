package com.vantage.bulls.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantage.bulls.dto.ExpiryDatesResponse;
import com.vantage.bulls.dto.OptionChainResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service("optionChainService")
public class OptionChainServiceImpl implements OptionChainService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionChainServiceImpl.class);

    @Value("${dhan.api.host}")
    private String hostName;

    @Value("${dhan.api.optionchain.endpoint}")
    private String optionChainApiEndpoint;

    @Value("${dhan.api.optionchain.expirylist.endpoint}")
    private String epxiryListApiEndpoint;

    @Value("${dhan.api.client-id}")
    private String clientId;

    @Value("${dhan.api.access-token}")
    private String accessToken;

    @Value("${dhan.api.oc.payload.underlyingScrip}")
    private int underlyingScrip;

    @Value("${dhan.api.oc.payload.underlyingSeg}")
    private String underlyingSeg;

    private String nearestExpiryDate;

    @PostConstruct
    public void init() {
        this.nearestExpiryDate = this.getNearestExpiryToCurrentDate();
        LOGGER.info("For today = {} nearest expiry calculated is = {}", LocalDate.now(), nearestExpiryDate);
    }

    private String getNearestExpiryToCurrentDate() {
        String nearestExpiryToCurrentDate = null;

        ExpiryDatesResponse expiryDatesResponse = null;

        String optionChainURL = getOptionChainURL();
        String optionsExpiryDatsListURL = optionChainURL + epxiryListApiEndpoint;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHttpHeaders();
        Map<String, Object> requestMap = getRequestMap();

        // Convert map to JSON using Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonRequest = objectMapper.writeValueAsString(requestMap);
            LOGGER.info("JSON Request: {}", jsonRequest);

            // Prepare HttpEntity with headers and body
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            expiryDatesResponse = restTemplate.postForObject(optionsExpiryDatsListURL, entity, ExpiryDatesResponse.class);
            LocalDate nearestExpiryDateObj = expiryDatesResponse.getExpiryDates().get(0);
            nearestExpiryToCurrentDate = nearestExpiryDateObj.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error occurred when fetching the oc, the exception being ", e);
        }

        return nearestExpiryToCurrentDate;
    }

    @Override
    public OptionChainResponse getOptionChain() {
        OptionChainResponse optionChainResponse = null;

        String optionChainURL = getOptionChainURL();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestMap = getRequestMap();
        requestMap.put("Expiry", nearestExpiryDate);

        // Convert map to JSON using Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonRequest = objectMapper.writeValueAsString(requestMap);
            LOGGER.info("JSON Request: {}",jsonRequest);

            // Prepare HttpEntity with headers and body
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            optionChainResponse = restTemplate.postForObject(optionChainURL, entity, OptionChainResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error occurred when fetching the oc, the exception being ", e);
        }
        return optionChainResponse;
    }

    private String getOptionChainURL() {
        String optionChainURL = hostName + optionChainApiEndpoint;
        LOGGER.info("Option Chain URL: {}",optionChainURL);
        return optionChainURL;
    }

    private Map<String, Object> getRequestMap() {
        // Create request map
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("UnderlyingScrip", underlyingScrip);
        requestMap.put("UnderlyingSeg", underlyingSeg);
        return requestMap;
    }

    private HttpHeaders getHttpHeaders() {
        // Set headers for clientId and accessToken
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client-id", clientId);
        headers.set("access-token", accessToken);
        return headers;
    }
}

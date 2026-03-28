package com.vantage.bulls.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantage.bulls.dto.OptionChainResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service("optionChainService")
public class OptionChainServiceImpl implements OptionChainService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionChainServiceImpl.class);

    private String hostName = "https://api.dhan.co";
    private String apiEndpoint = "/v2/optionchain";
    private String clientId = "1107674922";
    private String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkaGFuIiwicGFydG5lcklkIjoiIiwiZXhwIjoxNzczODk5NTYyLCJpYXQiOjE3NzM4MTMxNjIsInRva2VuQ29uc3VtZXJUeXBlIjoiU0VMRiIsIndlYmhvb2tVcmwiOiIiLCJkaGFuQ2xpZW50SWQiOiIxMTA3Njc0OTIyIn0.nYQM_Nl3TalO-G5nh1cUgr0uP169jkCy_9JvW5aJ6ad0fk_MBWiqXYvWl1DxwCL3mk7NyICeXafBg3hXb8lBow";


    @Override
    public OptionChainResponse getOptionChain() {
        OptionChainResponse optionChainResponse = null;

        String optionChainURL = hostName + apiEndpoint;
        LOGGER.info("Option Chain URL: {}",optionChainURL);
        RestTemplate restTemplate = new RestTemplate();

        // Set headers for clientId and accessToken
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client-id", clientId);
        headers.set("access-token", accessToken);

        // Create request map
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("UnderlyingScrip", 26000);
        requestMap.put("UnderlyingSeg", "NSE_FNO");
        requestMap.put("Expiry", "2026-03-24");

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
}

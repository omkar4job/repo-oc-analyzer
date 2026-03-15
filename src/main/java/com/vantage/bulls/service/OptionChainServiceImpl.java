package com.vantage.bulls.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantage.bulls.dto.OptionChainResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service("optionChainService")
public class OptionChainServiceImpl implements OptionChainService {

    private String hostName = "https://api.dhan.co";
    private String apiEndpoint = "/v2/optionchain";
    private String clientId = "1107674922";
    private String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkaGFuIiwicGFydG5lcklkIjoiIiwiZXhwIjoxNzczMDY3NDQ4LCJpYXQiOjE3NzI5ODEwNDgsInRva2VuQ29uc3VtZXJUeXBlIjoiU0VMRiIsIndlYmhvb2tVcmwiOiIiLCJkaGFuQ2xpZW50SWQiOiIxMTA3Njc0OTIyIn0.wMBDr1LxFJFo_XQLDyl49iG4eFuCTzRLQSCi3f7q0g-NvctzYqaBNS8DY7Dv_2fxNyHlZeUE8i27puxohmZDRg";


    @Override
    public OptionChainResponse getOptionChain() {
        OptionChainResponse optionChainResponse = null;

        String optionChainURL = hostName + apiEndpoint;
        System.out.println("Option Chain URL: " + optionChainURL);
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
        requestMap.put("Expiry", "2026-03-10");

        // Convert map to JSON using Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonRequest = objectMapper.writeValueAsString(requestMap);
            System.out.println("JSON Request: " + jsonRequest);

            // Prepare HttpEntity with headers and body
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            optionChainResponse = restTemplate.postForObject(optionChainURL, entity, OptionChainResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return optionChainResponse;
    }
}

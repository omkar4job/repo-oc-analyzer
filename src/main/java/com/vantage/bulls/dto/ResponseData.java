package com.vantage.bulls.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ResponseData {
    @JsonProperty("last_price")
    public Double lastPrice;
    
    // Using a Map because strike prices are dynamic keys
    public Map<String, OptionStrikeData> oc;

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Map<String, OptionStrikeData> getOc() {
        return oc;
    }

    public void setOc(Map<String, OptionStrikeData> oc) {
        this.oc = oc;
    }
}
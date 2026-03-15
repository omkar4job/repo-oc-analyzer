package com.vantage.bulls.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptionDetails {
    @JsonProperty("security_id")
    public Long securityId;
    
    @JsonProperty("average_price")
    public Double averagePrice;
    
    @JsonProperty("last_price")
    public Double lastPrice;
    
    @JsonProperty("implied_volatility")
    public Double impliedVolatility;
    
    public Long oi;
    public Long volume;
    
    @JsonProperty("previous_oi")
    public Long previousOi;
    
    @JsonProperty("previous_volume")
    public Long previousVolume;
    
    @JsonProperty("previous_close_price")
    public Double previousClosePrice;
    
    @JsonProperty("top_ask_price")
    public Double topAskPrice;
    
    @JsonProperty("top_ask_quantity")
    public Long topAskQuantity;
    
    @JsonProperty("top_bid_price")
    public Double topBidPrice;
    
    @JsonProperty("top_bid_quantity")
    public Long topBidQuantity;
    
    public OptionGreeks greeks;
}
package com.vantage.bulls.dto;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpiryDatesResponse {

    @JsonProperty("data")
    private List<LocalDate> expiryDates;

    @JsonProperty("status")
    private String status;

    // Default Constructor
    public ExpiryDatesResponse() {}

    // Getters and Setters
    public List<LocalDate> getExpiryDates() {
        return expiryDates;
    }

    public void setExpiryDates(List<LocalDate> expiryDates) {
        this.expiryDates = expiryDates;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExpiryDatesResponse{" +
                "expiryDates=" + expiryDates +
                ", status='" + status + '\'' +
                '}';
    }
}
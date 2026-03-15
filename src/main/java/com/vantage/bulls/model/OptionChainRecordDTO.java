package com.vantage.bulls.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="nifty_option_chain")
public class OptionChainRecordDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "spot_price")
    private Float spotPrice;

    @Column(name = "strike_price")
    private String strikePrice;

    // Call Options (CE)
    @Column(name = "ce_ltp")
    private Float ceLTP;

    @Column(name = "ce_volume")
    private Long ceVolume;

    @Column(name = "ce_oi")
    private Long ceOi;

    @Column(name = "ce_oi_change")
    private Long ceOiChange;

    @Column(name = "ce_delta")
    private Float ceDelta;

    @Column(name = "ce_theta")
    private Float ceTheta;

    @Column(name = "ce_gamma")
    private Float ceGamma;

    @Column(name = "ce_vega")
    private Float ceVega;

    @Column(name = "ce_implied_volatility")
    private Float ceImpliedVolatility;

    @Column(name = "ce_prev_oi")
    private Long cePrevOi;

    @Column(name = "ce_prev_close_price")
    private Float cePrevClosePrice;

    @Column(name = "ce_prev_volume")
    private Long cePrevVolume;

    @Column(name = "ce_avg_price")
    private Float ceAvgPrice;

    // Put Options (PE)
    @Column(name = "pe_ltp")
    private Float peLTP;

    @Column(name = "pe_volume")
    private Long peVolume;

    @Column(name = "pe_oi")
    private Long peOi;

    @Column(name = "pe_oi_change")
    private Long peOiChange;

    @Column(name = "pe_delta")
    private Float peDelta;

    @Column(name = "pe_theta")
    private Float peTheta;

    @Column(name = "pe_gamma")
    private Float peGamma;

    @Column(name = "pe_vega")
    private Float peVega;

    @Column(name = "pe_implied_volatility")
    private Float peImpliedVolatility;

    @Column(name = "pe_prev_oi")
    private Long pePrevOi;

    @Column(name = "pe_prev_close_price")
    private Float pePrevClosePrice;

    @Column(name = "pe_prev_volume")
    private Long pePrevVolume;

    @Column(name = "pe_avg_price")
    private Float peAvgPrice;

    // Ratios & Diffs
    @Column(name = "pcr")
    private Integer pcr;

    @Column(name = "pc_oi_difference")
    private Long pcOiDiff;

    @Column(name = "pc_oi_change_difference")
    private Long pcOiChangeDiff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Float getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(Float spotPrice) {
        this.spotPrice = spotPrice;
    }

    public String getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(String strikePrice) {
        this.strikePrice = strikePrice;
    }

    public Float getCeLTP() {
        return ceLTP;
    }

    public void setCeLTP(Float ceLTP) {
        this.ceLTP = ceLTP;
    }

    public Long getCeVolume() {
        return ceVolume;
    }

    public void setCeVolume(Long ceVolume) {
        this.ceVolume = ceVolume;
    }

    public Long getCeOi() {
        return ceOi;
    }

    public void setCeOi(Long ceOi) {
        this.ceOi = ceOi;
    }

    public Long getCeOiChange() {
        return ceOiChange;
    }

    public void setCeOiChange(Long ceOiChange) {
        this.ceOiChange = ceOiChange;
    }

    public Float getCeDelta() {
        return ceDelta;
    }

    public void setCeDelta(Float ceDelta) {
        this.ceDelta = ceDelta;
    }

    public Float getCeTheta() {
        return ceTheta;
    }

    public void setCeTheta(Float ceTheta) {
        this.ceTheta = ceTheta;
    }

    public Float getCeGamma() {
        return ceGamma;
    }

    public void setCeGamma(Float ceGamma) {
        this.ceGamma = ceGamma;
    }

    public Float getCeVega() {
        return ceVega;
    }

    public void setCeVega(Float ceVega) {
        this.ceVega = ceVega;
    }

    public Float getCeImpliedVolatility() {
        return ceImpliedVolatility;
    }

    public void setCeImpliedVolatility(Float ceImpliedVolatility) {
        this.ceImpliedVolatility = ceImpliedVolatility;
    }

    public Long getCePrevOi() {
        return cePrevOi;
    }

    public void setCePrevOi(Long cePrevOi) {
        this.cePrevOi = cePrevOi;
    }

    public Float getCePrevClosePrice() {
        return cePrevClosePrice;
    }

    public void setCePrevClosePrice(Float cePrevClosePrice) {
        this.cePrevClosePrice = cePrevClosePrice;
    }

    public Long getCePrevVolume() {
        return cePrevVolume;
    }

    public void setCePrevVolume(Long cePrevVolume) {
        this.cePrevVolume = cePrevVolume;
    }

    public Float getCeAvgPrice() {
        return ceAvgPrice;
    }

    public void setCeAvgPrice(Float ceAvgPrice) {
        this.ceAvgPrice = ceAvgPrice;
    }

    public Float getPeLTP() {
        return peLTP;
    }

    public void setPeLTP(Float peLTP) {
        this.peLTP = peLTP;
    }

    public Long getPeVolume() {
        return peVolume;
    }

    public void setPeVolume(Long peVolume) {
        this.peVolume = peVolume;
    }

    public Long getPeOi() {
        return peOi;
    }

    public void setPeOi(Long peOi) {
        this.peOi = peOi;
    }

    public Long getPeOiChange() {
        return peOiChange;
    }

    public void setPeOiChange(Long peOiChange) {
        this.peOiChange = peOiChange;
    }

    public Float getPeDelta() {
        return peDelta;
    }

    public void setPeDelta(Float peDelta) {
        this.peDelta = peDelta;
    }

    public Float getPeTheta() {
        return peTheta;
    }

    public void setPeTheta(Float peTheta) {
        this.peTheta = peTheta;
    }

    public Float getPeGamma() {
        return peGamma;
    }

    public void setPeGamma(Float peGamma) {
        this.peGamma = peGamma;
    }

    public Float getPeVega() {
        return peVega;
    }

    public void setPeVega(Float peVega) {
        this.peVega = peVega;
    }

    public Float getPeImpliedVolatility() {
        return peImpliedVolatility;
    }

    public void setPeImpliedVolatility(Float peImpliedVolatility) {
        this.peImpliedVolatility = peImpliedVolatility;
    }

    public Long getPePrevOi() {
        return pePrevOi;
    }

    public void setPePrevOi(Long pePrevOi) {
        this.pePrevOi = pePrevOi;
    }

    public Float getPePrevClosePrice() {
        return pePrevClosePrice;
    }

    public void setPePrevClosePrice(Float pePrevClosePrice) {
        this.pePrevClosePrice = pePrevClosePrice;
    }

    public Long getPePrevVolume() {
        return pePrevVolume;
    }

    public void setPePrevVolume(Long pePrevVolume) {
        this.pePrevVolume = pePrevVolume;
    }

    public Float getPeAvgPrice() {
        return peAvgPrice;
    }

    public void setPeAvgPrice(Float peAvgPrice) {
        this.peAvgPrice = peAvgPrice;
    }

    public Integer getPcr() {
        return pcr;
    }

    public void setPcr(Integer pcr) {
        this.pcr = pcr;
    }

    public Long getPcOiDiff() {
        return pcOiDiff;
    }

    public void setPcOiDiff(Long pcOiDiff) {
        this.pcOiDiff = pcOiDiff;
    }

    public Long getPcOiChangeDiff() {
        return pcOiChangeDiff;
    }

    public void setPcOiChangeDiff(Long pcOiChangeDiff) {
        this.pcOiChangeDiff = pcOiChangeDiff;
    }
}

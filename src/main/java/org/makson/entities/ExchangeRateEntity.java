package org.makson.entities;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRateEntity {
    private Long id;
    private CurrencyEntity baseCurrencyId;
    private CurrencyEntity targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRateEntity(Long id, CurrencyEntity baseCurrencyId, CurrencyEntity targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyEntity getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(CurrencyEntity baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public CurrencyEntity getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(CurrencyEntity targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateEntity that = (ExchangeRateEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                '}';
    }
}

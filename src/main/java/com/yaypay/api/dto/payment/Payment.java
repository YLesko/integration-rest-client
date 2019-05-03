package com.yaypay.api.dto.payment;

import com.yaypay.api.dto.core.Integration;

import java.math.BigDecimal;
import java.util.List;

/*************************************************************************
 * * Yaypay CONFIDENTIAL   2018
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 * Author : AKuzovchikov
 * Date Created: 7/25/2018 17:49
 */
public class Payment {
    private Integration customer;
    private String paymentDate;
    private BigDecimal amount;
    private String channel;
    private BigDecimal exchangeRate;
    private String currency;
    private BigDecimal appliedAmount;
    private List<PaymentItem> applyList;
    private String referenceNumber;
    private String externalId;

    @java.beans.ConstructorProperties({"customer", "paymentDate", "amount", "channel", "exchangeRate", "currency", "appliedAmount", "applyList", "referenceNumber", "externalId"})
    Payment(Integration customer, String paymentDate, BigDecimal amount, String channel, BigDecimal exchangeRate, String currency, BigDecimal appliedAmount, List<PaymentItem> applyList, String referenceNumber, String externalId) {
        this.customer = customer;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.channel = channel;
        this.exchangeRate = exchangeRate;
        this.currency = currency;
        this.appliedAmount = appliedAmount;
        this.applyList = applyList;
        this.referenceNumber = referenceNumber;
        this.externalId = externalId;
    }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public Integration getCustomer() {
        return this.customer;
    }

    public String getPaymentDate() {
        return this.paymentDate;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getChannel() {
        return this.channel;
    }

    public BigDecimal getExchangeRate() {
        return this.exchangeRate;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getAppliedAmount() {
        return this.appliedAmount;
    }

    public List<PaymentItem> getApplyList() {
        return this.applyList;
    }

    public String getReferenceNumber() {
        return this.referenceNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    public static class PaymentBuilder {
        private Integration customer;
        private String paymentDate;
        private BigDecimal amount;
        private String channel;
        private BigDecimal exchangeRate;
        private String currency;
        private BigDecimal appliedAmount;
        private List<PaymentItem> applyList;
        private String referenceNumber;
        private String externalId;

        PaymentBuilder() {
        }

        public Payment.PaymentBuilder customer(Integration customer) {
            this.customer = customer;
            return this;
        }

        public Payment.PaymentBuilder paymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Payment.PaymentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Payment.PaymentBuilder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Payment.PaymentBuilder exchangeRate(BigDecimal exchangeRate) {
            this.exchangeRate = exchangeRate;
            return this;
        }

        public Payment.PaymentBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Payment.PaymentBuilder appliedAmount(BigDecimal appliedAmount) {
            this.appliedAmount = appliedAmount;
            return this;
        }

        public Payment.PaymentBuilder applyList(List<PaymentItem> applyList) {
            this.applyList = applyList;
            return this;
        }

        public Payment.PaymentBuilder referenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
            return this;
        }

        public PaymentBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Payment build() {
            return new Payment(customer, paymentDate, amount, channel, exchangeRate, currency, appliedAmount, applyList, referenceNumber, externalId);
        }

        public String toString() {
            return "Payment.PaymentBuilder(customer=" + this.customer + ", paymentDate=" + this.paymentDate + ", amount=" + this.amount + ", channel=" + this.channel + ", exchangeRate=" + this.exchangeRate + ", currency=" + this.currency + ", appliedAmount=" + this.appliedAmount + ", applyList=" + this.applyList + ", referenceNumber=" + this.referenceNumber + ", externalId=" + this.externalId + ")";
        }
    }
}

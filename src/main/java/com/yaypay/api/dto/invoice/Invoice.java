package com.yaypay.api.dto.invoice;

import com.yaypay.api.dto.core.Integration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
public class Invoice {
    private Integration customer;
    private Integration file;
    private String invoiceDate;
    private String dueDate;
    private InvoicePaidStatus status;
    private String currency;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal paid;
    private BigDecimal total;
    private BigDecimal exchangeRate;
    private String notes;
    private String invoiceNumber;
    private List<InvoiceItem> items;
    private Map<String, Object> customFields;
    private String closeDate;
    private String pdfUrl;
    private String terms;
    private InvoiceDistributionChannel distributionChannel;
    private Integration contact;
    private String contactEmail;
    private String salesRepresentative;


    @java.beans.ConstructorProperties({"customer", "file", "invoiceDate", "dueDate", "status", "currency", "discount", "tax", "paid", "total", "exchangeRate", "notes", "invoiceNumber", "items", "customFields", "closeDate", "pdfUrl", "terms", "distributionChannel", "contact", "contactEmail", "salesRepresentative"})
    Invoice(Integration customer, Integration file, String invoiceDate, String dueDate, InvoicePaidStatus status, String currency, BigDecimal discount, BigDecimal tax, BigDecimal paid, BigDecimal total, BigDecimal exchangeRate, String notes, String invoiceNumber, List<InvoiceItem> items, Map<String, Object> customFields, String closeDate, String pdfUrl, String terms, InvoiceDistributionChannel distributionChannel, Integration contact, String contactEmail, String salesRepresentative) {
        this.customer = customer;
        this.file = file;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.status = status;
        this.currency = currency;
        this.discount = discount;
        this.tax = tax;
        this.paid = paid;
        this.total = total;
        this.exchangeRate = exchangeRate;
        this.notes = notes;
        this.invoiceNumber = invoiceNumber;
        this.items = items;
        this.customFields = customFields;
        this.closeDate = closeDate;
        this.pdfUrl = pdfUrl;
        this.terms = terms;
        this.distributionChannel = distributionChannel;
        this.contact = contact;
        this.contactEmail = contactEmail;
        this.salesRepresentative = salesRepresentative;
    }

    public static InvoiceBuilder builder() {
        return new InvoiceBuilder();
    }

    public Integration getCustomer() {
        return this.customer;
    }

    public Integration getFile() {
        return this.file;
    }

    public String getInvoiceDate() {
        return this.invoiceDate;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public InvoicePaidStatus getStatus() {
        return this.status;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public BigDecimal getTax() {
        return this.tax;
    }

    public BigDecimal getPaid() {
        return this.paid;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public BigDecimal getExchangeRate() {
        return this.exchangeRate;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public List<InvoiceItem> getItems() {
        return this.items;
    }

    public Map<String, Object> getCustomFields() {
        return this.customFields;
    }

    public String getCloseDate() {
        return this.closeDate;
    }

    public String getPdfUrl() {
        return this.pdfUrl;
    }

    public String getTerms() {
        return this.terms;
    }

    public InvoiceDistributionChannel getDistributionChannel() {
        return this.distributionChannel;
    }

    public Integration getContact() {
        return this.contact;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public String getSalesRepresentative() {
        return salesRepresentative;
    }

    public Invoice setCustomer(Integration customer) {
        this.customer = customer;
        return this;
    }

    public Invoice setFile(Integration file) {
        this.file = file;
        return this;
    }

    public Invoice setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public Invoice setDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Invoice setStatus(InvoicePaidStatus status) {
        this.status = status;
        return this;
    }

    public Invoice setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Invoice setDiscount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }

    public Invoice setTax(BigDecimal tax) {
        this.tax = tax;
        return this;
    }

    public Invoice setPaid(BigDecimal paid) {
        this.paid = paid;
        return this;
    }

    public Invoice setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public Invoice setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public Invoice setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Invoice setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public Invoice setItems(List<InvoiceItem> items) {
        this.items = items;
        return this;
    }

    public Invoice setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
        return this;
    }

    public Invoice setCloseDate(String closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public Invoice setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
        return this;
    }

    public Invoice setTerms(String terms) {
        this.terms = terms;
        return this;
    }

    public Invoice setDistributionChannel(InvoiceDistributionChannel distributionChannel) {
        this.distributionChannel = distributionChannel;
        return this;
    }

    public Invoice setContact(Integration contact) {
        this.contact = contact;
        return this;
    }

    public Invoice setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public static class InvoiceBuilder {
        private Integration customer;
        private Integration file;
        private String invoiceDate;
        private String dueDate;
        private InvoicePaidStatus status;
        private String currency;
        private BigDecimal discount;
        private BigDecimal tax;
        private BigDecimal paid;
        private BigDecimal total;
        private BigDecimal exchangeRate;
        private String notes;
        private String invoiceNumber;
        private List<InvoiceItem> items;
        private Map<String, Object> customFields;
        private String closeDate;
        private String pdfUrl;
        private String terms;
        private InvoiceDistributionChannel distributionChannel;
        private Integration contact;
        private String contactEmail;
        private String salesRepresentative;

        InvoiceBuilder() {
        }

        public Invoice.InvoiceBuilder customer(Integration customer) {
            this.customer = customer;
            return this;
        }

        public Invoice.InvoiceBuilder file(Integration file) {
            this.file = file;
            return this;
        }

        public Invoice.InvoiceBuilder invoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        public Invoice.InvoiceBuilder dueDate(String dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Invoice.InvoiceBuilder status(InvoicePaidStatus status) {
            this.status = status;
            return this;
        }

        public Invoice.InvoiceBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Invoice.InvoiceBuilder discount(BigDecimal discount) {
            this.discount = discount;
            return this;
        }

        public Invoice.InvoiceBuilder tax(BigDecimal tax) {
            this.tax = tax;
            return this;
        }

        public Invoice.InvoiceBuilder paid(BigDecimal paid) {
            this.paid = paid;
            return this;
        }

        public Invoice.InvoiceBuilder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Invoice.InvoiceBuilder exchangeRate(BigDecimal exchangeRate) {
            this.exchangeRate = exchangeRate;
            return this;
        }

        public Invoice.InvoiceBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Invoice.InvoiceBuilder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Invoice.InvoiceBuilder items(List<InvoiceItem> items) {
            this.items = items;
            return this;
        }

        public Invoice.InvoiceBuilder customFields(Map<String, Object> customFields) {
            this.customFields = customFields;
            return this;
        }

        public Invoice.InvoiceBuilder closeDate(String closeDate) {
            this.closeDate = closeDate;
            return this;
        }

        public Invoice.InvoiceBuilder pdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public Invoice.InvoiceBuilder terms(String terms) {
            this.terms = terms;
            return this;
        }

        public Invoice.InvoiceBuilder distributionChannel(InvoiceDistributionChannel distributionChannel) {
            this.distributionChannel = distributionChannel;
            return this;
        }

        public Invoice.InvoiceBuilder contact(Integration contact) {
            this.contact = contact;
            return this;
        }

        public Invoice.InvoiceBuilder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public Invoice.InvoiceBuilder salesRepresentative(String salesRepresentative) {
            this.salesRepresentative = salesRepresentative;
            return this;
        }

        public Invoice build() {
            return new Invoice(customer, file, invoiceDate, dueDate, status, currency, discount, tax, paid, total, exchangeRate, notes, invoiceNumber, items, customFields, closeDate, pdfUrl, terms, distributionChannel, contact, contactEmail, salesRepresentative);
        }

        public String toString() {
            return "Invoice.InvoiceBuilder(customer=" + this.customer + ", file=" + this.file + ", invoiceDate=" + this.invoiceDate + ", dueDate=" + this.dueDate + ", status=" + this.status + ", currency=" + this.currency + ", discount=" + this.discount + ", tax=" + this.tax + ", paid=" + this.paid + ", total=" + this.total + ", exchangeRate=" + this.exchangeRate + ", notes=" + this.notes + ", invoiceNumber=" + this.invoiceNumber + ", items=" + this.items + ", customFields=" + this.customFields + ", closeDate=" + this.closeDate + ", pdfUrl=" + this.pdfUrl + ", terms=" + this.terms + ", distributionChannel=" + this.distributionChannel + ", contact=" + this.contact + ", contactEmail=" + this.contactEmail + ", salesRepresentative=" + salesRepresentative + ")";
        }
    }
}

package com.yaypay.integration.service.api;

import com.yaypay.api.dto.account.AccountRequest;
import com.yaypay.api.dto.adjustment.AdjustmentRequest;
import com.yaypay.api.dto.contact.ContactRequest;
import com.yaypay.api.dto.content.ContentRequest;
import com.yaypay.api.dto.core.DeleteEntity;
import com.yaypay.api.dto.creditmemo.CreditMemoRequest;
import com.yaypay.api.dto.currency.CurrencyRequest;
import com.yaypay.api.dto.customer.CustomerRequest;
import com.yaypay.api.dto.customer.CustomerResponse;
import com.yaypay.api.dto.external_company.ExternalCompanyRequest;
import com.yaypay.api.dto.external_contact.ExternalContactRequest;
import com.yaypay.api.dto.invoice.InvoiceRequest;
import com.yaypay.api.dto.log.LoggingMessage;
import com.yaypay.api.dto.log.SyncEntity;
import com.yaypay.api.dto.page.LoggingMessageResponse;
import com.yaypay.api.dto.payment.PaymentRequest;
import com.yaypay.api.dto.sales.SalesRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/*************************************************************************
 * * Yaypay CONFIDENTIAL   2018
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 * Author : <a href="mailto:elesko@yaypay.com">Egor Lesko</a>
 * Date Created: 8/23/18 10:53
 */
public interface IntegrationService extends AutoCloseable {

    Long startTransaction(String apiKey, Date startPeriod, Date endPeriod, String sourceSystemType, boolean transactionIsIrregular);

    void finishTransaction(Long transactionId, String apiKey);

    void finishTransaction(Long transactionId, String apiKey, boolean isHistorical);

    List<String> getOpenInvoiceIds(Integer bizId, String apiKey);

    List<String> getOpenInvoiceWithoutContentIds(Integer bizId, String apiKey);

    List<String> getInvoiceWithIncorrectPaidSum(Integer bizId, String apiKey);

    List<String> getOpenAdjustmentsIds(Integer bizId, String apiKey);

    List<String> getOpenPaymentsIds(Integer bizId, String apiKey);

    void createOrUpdateInvoices(Long transactionId, List<InvoiceRequest> invoiceRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateContacts(Long transactionId, List<ContactRequest> contactRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateCustomers(Long transactionId, List<CustomerRequest> customerRequests, String apiKey)
            throws InterruptedException, ExecutionException;

    void createOrUpdatePayments(Long transactionId, List<PaymentRequest> paymentRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateCreditMemos(Long transactionId, List<CreditMemoRequest> creditMemoRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateAdjustment(Long transactionId,
                                  List<AdjustmentRequest> adjustmentRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateExternalCompanies(Long transactionId,
                                         List<ExternalCompanyRequest> externalCompanyRequests,
                                         String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateExternalContacts(Long transactionId,
                                        List<ExternalContactRequest> externalContactRequests,
                                        String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateSales(Long transactionId, List<SalesRequest> salesRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateCurrencies(Long transactionId, List<CurrencyRequest> currencyRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateContents(Long transactionId, List<ContentRequest> contentRequests, String apiKey) throws InterruptedException, ExecutionException;

    void createOrUpdateContent(Long transactionId, ContentRequest contentRequests, String apiKey);

    void createOrUpdateAccounts(Long transactionId, List<AccountRequest> contentRequests, String apiKey) throws InterruptedException, ExecutionException;

    Collection<CustomerResponse> getCustomers(Integer bizId, String apiKey, String sourceSystemType, boolean withOpenBalancesOnly);

    void deleteCustomers(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deleteInvoices(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deletePayments(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deleteCreditMemos(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deleteAdjustments(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deleteContacts(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    Set<String> deleteEntity(SyncEntity syncEntityType, List<DeleteEntity> deleteEntities, Long transactionId, String apiKey) throws ExecutionException, InterruptedException;

    List<String> getActiveEntityIds(SyncEntity syncEntityType, Integer bizId, String apiKey, String sourceSystemType);

    void sendLoggingMessage(String apiKey, Long transactionId, LoggingMessage loggingMessage);

    LoggingMessageResponse getLoggingMessagesByTransactionIds(String apiKey, List<Long> transactionIds, int page, int size);
}

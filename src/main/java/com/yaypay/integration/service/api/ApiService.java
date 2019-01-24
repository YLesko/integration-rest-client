package com.yaypay.integration.service.api;

import com.yaypay.api.dto.adjustment.AdjustmentRequest;
import com.yaypay.api.dto.contact.ContactRequest;
import com.yaypay.api.dto.content.ContentRequest;
import com.yaypay.api.dto.core.DeleteEntity;
import com.yaypay.api.dto.core.DeleteEntityRequest;
import com.yaypay.api.dto.core.EntityListRequest;
import com.yaypay.api.dto.creditmemo.CreditMemoRequest;
import com.yaypay.api.dto.currency.CurrencyRequest;
import com.yaypay.api.dto.customer.CustomerRequest;
import com.yaypay.api.dto.customer.CustomerResponse;
import com.yaypay.api.dto.external_company.ExternalCompanyRequest;
import com.yaypay.api.dto.external_contact.ExternalContactRequest;
import com.yaypay.api.dto.invoice.InvoiceRequest;
import com.yaypay.api.dto.log.LoggingMessage;
import com.yaypay.api.dto.log.SyncEntity;
import com.yaypay.api.dto.payment.PaymentRequest;
import com.yaypay.api.dto.sales.SalesRequest;
import com.yaypay.api.dto.transaction.UploadTransactionDTO;
import com.yaypay.integration.service.httpclient.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yaypay.integration.util.UrlUtil.getUrlPrefx;
import static com.yaypay.util.DateFormatUtil.dateToIso8601UtcString;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.ListUtils.partition;

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

public final class ApiService implements IntegrationService {
    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    private static final int CHUNK_SIZE = 2000;
    private static final String API_KEY_HEADER = "ApiKey";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PROCESSED_CHUNK_FROM = "Processed chunk {} from {}";

    private final ExecutorService apiExecutorService = newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    private final HttpClient httpClient;
    private final String apiUrl;
    private final String authorizationToken;

    public ApiService(String apiUrl, String authorizationToken) {
        this.apiUrl = apiUrl;
        this.authorizationToken = authorizationToken;
        this.httpClient = new HttpClient();
    }

    public ApiService(String apiUrl, String authorizationToken, HttpClient httpClient) {
        this.apiUrl = apiUrl;
        this.authorizationToken = authorizationToken;
        this.httpClient = httpClient;
    }

    @Override
    public Long startTransaction(String apiKey, Date startPeriod, Date endPeriod, String sourceSystemType, boolean transactionIsIrregular) {
        String url = this.apiUrl + "/batch/start?start_period=" +
                dateToIso8601UtcString(startPeriod) + "&end_period="
                + dateToIso8601UtcString(endPeriod) + "&source_system=" + sourceSystemType +
                "&is_regular=" + (!transactionIsIrregular);

        UploadTransactionDTO uploadTransactionDTOResponseEntity =
                httpClient.post(url, null, UploadTransactionDTO.class, buildAuthenticationHeaders(apiKey));
        Long id = uploadTransactionDTOResponseEntity.getId();
        log.info("Start transaction with id: {}", id);
        return id;
    }

    @Override
    public void finishTransaction(Long transactionId, String apiKey) {
        finishTransaction(transactionId, apiKey, false);
    }

    @Override
    public void finishTransaction(Long transactionId, String apiKey, boolean isHistorical) {
        log.info("Start finish transaction with id: {}", transactionId);
        Map<String, String> authenticationHeaders = buildAuthenticationHeaders(apiKey);
        String url = this.apiUrl + "/batch/finish?transaction_id=" + transactionId + "&is_historical=" + isHistorical;
        httpClient.postForLocation(url, null, authenticationHeaders);
        log.info("End transaction with id: {}", transactionId);
    }

    @Override
    public List<String> getOpenInvoiceIds(Integer bizId, String apiKey) {
        String url = calculateUrlForSync("/invoices/open", bizId);
        String[] invoices = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return Arrays.asList(invoices);
    }

    @Override
    public List<String> getOpenInvoiceWithoutContentIds(Integer bizId, String apiKey) {
        String url = calculateUrlForSync("/invoices/open_without_content", bizId);
        String[] invoices = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return Arrays.asList(invoices);
    }

    @Override
    public List<String> getInvoiceWithIncorrectPaidSum(Integer bizId, String apiKey) {
        String url = calculateUrlForSync("/invoices/open_with_incorrect_paid", bizId);
        String[] invoices = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return Arrays.asList(invoices);
    }

    @Override
    public List<String> getOpenAdjustmentsIds(Integer bizId, String apiKey) {
        String url = calculateUrlForSync("/adjustments/open", bizId);
        String[] adjustments = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return Arrays.asList(adjustments);
    }

    @Override
    public List<String> getOpenPaymentsIds(Integer bizId, String apiKey) {
        String url = calculateUrlForSync("/payments/open", bizId);
        String[] payments = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return Arrays.asList(payments);
    }

    @Override
    public void createOrUpdateInvoices(Long transactionId, List<InvoiceRequest> invoiceRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<InvoiceRequest>> lists = partition(invoiceRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Invoices chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<InvoiceRequest> currentChunk : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<InvoiceRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentChunk);
                String url = this.apiUrl + "/invoices?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;

            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateContacts(Long transactionId, List<ContactRequest> contactRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<ContactRequest>> lists = partition(contactRequests, CHUNK_SIZE);
        int i = 0;
        final int size = lists.size();
        log.info("Contact chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<ContactRequest> currentChunk : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<ContactRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentChunk);
                String url = this.apiUrl + "/contacts?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateCustomers(Long transactionId, List<CustomerRequest> customerRequests, String apiKey)
            throws InterruptedException, ExecutionException {
        List<List<CustomerRequest>> partition = partition(customerRequests, CHUNK_SIZE);
        int i = 0;
        int size = partition.size();
        log.info("Customers chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<CustomerRequest> currentChunk : partition) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<CustomerRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentChunk);
                String url = this.apiUrl + "/customers?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdatePayments(Long transactionId, List<PaymentRequest> paymentRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<PaymentRequest>> lists = partition(paymentRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Payments chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<PaymentRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<PaymentRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/payments?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateCreditMemos(Long transactionId, List<CreditMemoRequest> creditMemoRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<CreditMemoRequest>> lists = partition(creditMemoRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Credit memo chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<CreditMemoRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<CreditMemoRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/credit-memo?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateAdjustment(Long transactionId,
                                         List<AdjustmentRequest> adjustmentRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<AdjustmentRequest>> lists = partition(adjustmentRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Adjustments chunks size -  {}, total size - {}", size, adjustmentRequests.size());
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<AdjustmentRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<AdjustmentRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/adjustments?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateExternalCompanies(Long transactionId,
                                                List<ExternalCompanyRequest> externalCompanyRequests,
                                                String apiKey) throws InterruptedException, ExecutionException {

        List<List<ExternalCompanyRequest>> lists = partition(externalCompanyRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("External companies chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<ExternalCompanyRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<ExternalCompanyRequest> externalCompanyRequestEntityListRequest = new EntityListRequest<>();
                externalCompanyRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/external-companies?transaction_id=" + transactionId;
                httpClient.postForLocation(url, externalCompanyRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);

    }

    @Override
    public void createOrUpdateExternalContacts(Long transactionId,
                                               List<ExternalContactRequest> externalContactRequests,
                                               String apiKey) throws InterruptedException, ExecutionException {
        List<List<ExternalContactRequest>> lists = partition(externalContactRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("External contacts chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<ExternalContactRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<ExternalContactRequest> externalContactRequestEntityListRequest = new EntityListRequest<>();
                externalContactRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/external-contacts?transaction_id=" + transactionId;
                httpClient.postForLocation(url, externalContactRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }


    @Override
    public void createOrUpdateSales(Long transactionId, List<SalesRequest> salesRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<SalesRequest>> lists = partition(salesRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Sales chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<SalesRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<SalesRequest> salesRequestEntityListRequest = new EntityListRequest<>();
                salesRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/sales?transaction_id=" + transactionId;
                httpClient.postForLocation(url, salesRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateCurrencies(Long transactionId, List<CurrencyRequest> currencyRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<CurrencyRequest>> lists = partition(currencyRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Currencies chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<CurrencyRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<CurrencyRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentPartition);
                String url = this.apiUrl + "/currencies?transaction_id=" + transactionId;
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateContents(Long transactionId, List<ContentRequest> contentRequests, String apiKey) throws InterruptedException, ExecutionException {
        List<List<ContentRequest>> lists = partition(contentRequests, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Contents chunks size: {}", size);
        List<Callable<Void>> tasks = new ArrayList<>(size);
        for (List<ContentRequest> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Void> task = () -> {
                EntityListRequest<ContentRequest> customerRequestEntityListRequest = new EntityListRequest<>();
                customerRequestEntityListRequest.setItems(currentPartition);
                String url = buildContentUrl(transactionId);
                httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return null;
            };
            tasks.add(task);
        }
        processAllTasks(tasks);
    }

    @Override
    public void createOrUpdateContent(Long transactionId, ContentRequest contentRequests, String apiKey) {
        EntityListRequest<ContentRequest> customerRequestEntityListRequest = new EntityListRequest<>();
        customerRequestEntityListRequest.setItems(Collections.singletonList(contentRequests));
        String url = buildContentUrl(transactionId);
        httpClient.postForLocation(url, customerRequestEntityListRequest, buildAuthenticationHeaders(apiKey));
    }

    private String buildContentUrl(Long transactionId) {
        return this.apiUrl + "/contents?transaction_id=" + transactionId;
    }


    @Override
    public Collection<CustomerResponse> getCustomers(Integer bizId, String apiKey, String sourceSystemType, boolean withOpenBalancesOnly) {
        String url = calculateUrlForSync("/" + getUrlPrefx(SyncEntity.CUSTOMERS), bizId)
                + "&source_system_type=" + sourceSystemType
                + "&open_balances_only=" + withOpenBalancesOnly;
        CustomerResponse[] responses = httpClient.get(url, CustomerResponse[].class, buildAuthenticationHeaders(apiKey));
        return responses != null ? Arrays.asList(responses) : Collections.emptyList();
    }

    @Override
    public void deleteCustomers(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.CUSTOMERS));
    }

    @Override
    public Set<String> deleteInvoices(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.INVOICES));
    }

    @Override
    public Set<String> deletePayments(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.PAYMENTS));
    }

    @Override
    public Set<String> deleteCreditMemos(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.CM));
    }

    @Override
    public Set<String> deleteAdjustments(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.ADJUSTMENTS));
    }

    @Override
    public Set<String> deleteContacts(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deletedDtos, transactionId, apiKey, getUrlPrefx(SyncEntity.CONTACTS));
    }

    private Set<String> deleteEntity(List<DeleteEntity> deletedDtos, Long transactionId, String apiKey, String urlPrefix) throws ExecutionException, InterruptedException {
        List<List<DeleteEntity>> lists = partition(deletedDtos, CHUNK_SIZE);
        int i = 0;
        int size = lists.size();
        log.info("Entity size: {}", size);
        List<Callable<Set<String>>> tasks = new ArrayList<>(size);
        for (List<DeleteEntity> currentPartition : lists) {
            final int chunkNumber = ++i;
            Callable<Set<String>> task = () -> {
                DeleteEntityRequest deleteRequest = new DeleteEntityRequest();
                deleteRequest.setDeletedEntities(currentPartition);
                String url = this.apiUrl + "/" + urlPrefix + "/delete?transaction_id=" + transactionId;
                String[] response = httpClient.post(url, deleteRequest, String[].class, buildAuthenticationHeaders(apiKey));
                log.info(PROCESSED_CHUNK_FROM, chunkNumber, size);
                return response != null ? new HashSet<>(Arrays.asList(response)) : new HashSet<>();
            };
            tasks.add(task);
        }
        List<Future<Set<String>>> futures = apiExecutorService.invokeAll(tasks);
        Set<String> result = new HashSet<>();
        for (Future<Set<String>> item : futures) {
            Set<String> resultOfTask = item.get();
            if (!isEmpty(resultOfTask)) {
                result.addAll(resultOfTask);
            }
        }
        return result;
    }

    @Override
    public Set<String> deleteEntity(SyncEntity syncEntityType, List<DeleteEntity> deleteEntities, Long transactionId, String apiKey) throws ExecutionException, InterruptedException {
        return deleteEntity(deleteEntities, transactionId, apiKey, getUrlPrefx(syncEntityType));
    }

    @Override
    public List<String> getActiveEntityIds(SyncEntity syncEntityType, Integer bizId, String apiKey, String sourceSystemType) {
        String url = calculateUrlForSync("/" + getUrlPrefx(syncEntityType) + "/active", bizId);
        url = url + "&sourceSystemType=" + sourceSystemType;
        String[] entities = httpClient.get(url, String[].class, buildAuthenticationHeaders(apiKey));
        return entities != null ? Arrays.asList(entities) : Collections.emptyList();
    }

    @Override
    public void sendLoggingMessage(String apiKey, Long transactionId, LoggingMessage loggingMessage) {
        String url = this.apiUrl + "/messages?transaction_id=" + transactionId;
        httpClient.postForLocation(url, loggingMessage, buildAuthenticationHeaders(apiKey));
    }

    private String calculateUrlForSync(String url, Integer bizId) {
        String requestUrl = this.apiUrl + url;
        if (bizId != null) {
            requestUrl = requestUrl + "?bizId=" + bizId;
        }
        return requestUrl;
    }

    private Map<String, String> buildAuthenticationHeaders(String apiKey) {
        HashMap<String, String> map = new HashMap<>();
        map.put(API_KEY_HEADER, apiKey);
        map.put(AUTHORIZATION_HEADER, this.authorizationToken);
        return map;
    }

    private void processAllTasks(List<Callable<Void>> tasks) throws InterruptedException, ExecutionException {
        List<Future<Void>> futures = apiExecutorService.invokeAll(tasks);
        for (Future<Void> item : futures) {
            item.get();
        }
    }
}

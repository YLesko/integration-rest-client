package com.yaypay.integration.service.api;

import com.yaypay.api.dto.adjustment.AdjustmentRequest;
import com.yaypay.api.dto.contact.ContactRequest;
import com.yaypay.api.dto.content.ContentRequest;
import com.yaypay.api.dto.core.DeleteEntity;
import com.yaypay.api.dto.core.DeleteEntityRequest;
import com.yaypay.api.dto.core.EntityListRequest;
import com.yaypay.api.dto.core.Integration;
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {
    private static final String API_URL = "http://localhost";
    private static final Long TRANSACTION_ID = 123L;
    private static final String API_KEY = "apiKey";
    private static final int BIZ_ID = 123;
    private static final String INTERNAL_ID = "iNTERNAl_iD";
    private static final String SOURCE_SYSTEM_TYPE = "NET_SUITE";
    private static final String DELETE_RESPONSE = "Delete_response";
    private static final String SOME_ID = "someId";

    @Mock
    private HttpClient httpClient;
    private ApiService apiService;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<EntityListRequest> entityListRequestCaptor;
    @Captor
    private ArgumentCaptor<DeleteEntityRequest> deleteEntityRequestArgumentCaptor;
    @Mock
    private LoggingMessage loggingMessage;

    @Before
    public void setUp() throws Exception {
        this.apiService = new ApiService(API_URL, "", httpClient);
    }

    @Ignore
    @Test
    public void startTransaction() {
        String expectedUrl = "http://localhost/batch/start?start_period=1969-12-31T22:00:00+00:00&end_period=2017-12-31T22:00:00+00:00&source_system=NET_SUITE&is_regular=false";
        when(httpClient.post(urlCaptor.capture(), eq(null), eq(UploadTransactionDTO.class), anyMap())).thenReturn(UploadTransactionDTO.builder().id(TRANSACTION_ID).build());
        Long transaction = apiService.startTransaction(API_KEY, getStartDate(), getEndDate(), SOURCE_SYSTEM_TYPE, true);
        assertEquals(TRANSACTION_ID, transaction);
        assertEquals(expectedUrl, urlCaptor.getValue());
    }


    @Test
    public void finishTransaction() {
        String expectedUrl = "http://localhost/batch/finish?transaction_id=123&is_historical=false";
        apiService.finishTransaction(TRANSACTION_ID, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), eq(null), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
    }

    @Test
    public void finishTransaction1() {
        String expectedUrl = "http://localhost/batch/finish?transaction_id=123&is_historical=true";
        apiService.finishTransaction(TRANSACTION_ID, API_KEY, true);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), eq(null), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
    }

    @Test
    public void getOpenInvoiceIds() {
        String expectedUrl = "http://localhost/invoices/open?bizId=123";
        String openInvoiceId = "someText";
        when(httpClient.get(urlCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{openInvoiceId});
        List<String> openInvoiceIds = apiService.getOpenInvoiceIds(BIZ_ID, API_KEY);
        assertEquals(1, openInvoiceIds.size());
        assertEquals(openInvoiceId, openInvoiceIds.get(0));
        assertEquals(expectedUrl, urlCaptor.getValue());
    }

    @Test
    public void createOrUpdateInvoices() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/invoices?transaction_id=123";
        ArrayList<InvoiceRequest> invoices = getInvoices();
        apiService.createOrUpdateInvoices(TRANSACTION_ID, invoices, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<InvoiceRequest> request = entityListRequestCaptor.getValue();
        List<InvoiceRequest> items = request.getItems();
        assertEquals(invoices.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateContacts() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/contacts?transaction_id=123";
        List<ContactRequest> contacts = getContacts();
        apiService.createOrUpdateContacts(TRANSACTION_ID, contacts, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<ContactRequest> request = entityListRequestCaptor.getValue();
        List<ContactRequest> items = request.getItems();
        assertEquals(contacts.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateCustomers() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/customers?transaction_id=123";
        List<CustomerRequest> requestList = getCustomersList();
        apiService.createOrUpdateCustomers(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<CustomerRequest> request = entityListRequestCaptor.getValue();
        List<CustomerRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdatePayments() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/payments?transaction_id=123";
        List<PaymentRequest> requestList = getPaymentRequest();
        apiService.createOrUpdatePayments(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<PaymentRequest> request = entityListRequestCaptor.getValue();
        List<PaymentRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateCreditMemos() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/credit-memo?transaction_id=123";
        List<CreditMemoRequest> requestList = getCreditMemos();
        apiService.createOrUpdateCreditMemos(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<CreditMemoRequest> request = entityListRequestCaptor.getValue();
        List<CreditMemoRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateAdjustment() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/adjustments?transaction_id=123";
        List<AdjustmentRequest> requestList = getAdjustments();
        apiService.createOrUpdateAdjustment(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<AdjustmentRequest> request = entityListRequestCaptor.getValue();
        List<AdjustmentRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateExternalCompanies() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/external-companies?transaction_id=123";
        List<ExternalCompanyRequest> requestList = getExternalCompanyRequest();
        apiService.createOrUpdateExternalCompanies(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<ExternalCompanyRequest> request = entityListRequestCaptor.getValue();
        List<ExternalCompanyRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateExternalContacts() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/external-contacts?transaction_id=123";
        List<ExternalContactRequest> requestList = getExternalContacts();
        apiService.createOrUpdateExternalContacts(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<ExternalContactRequest> request = entityListRequestCaptor.getValue();
        List<ExternalContactRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateSales() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/sales?transaction_id=123";
        List<SalesRequest> requestList = getSalesRequest();
        apiService.createOrUpdateSales(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<SalesRequest> request = entityListRequestCaptor.getValue();
        List<SalesRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateCurrencies() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/currencies?transaction_id=123";
        List<CurrencyRequest> requestList = getCurrencyRequest();
        apiService.createOrUpdateCurrencies(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<CurrencyRequest> request = entityListRequestCaptor.getValue();
        List<CurrencyRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }

    @Test
    public void createOrUpdateContents() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/contents?transaction_id=123";
        List<ContentRequest> requestList = getContentRequest();
        apiService.createOrUpdateContents(TRANSACTION_ID, requestList, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<ContentRequest> request = entityListRequestCaptor.getValue();
        List<ContentRequest> items = request.getItems();
        assertEquals(requestList.get(0), items.get(0));
    }


    @Test
    public void createOrUpdateContent() {
        String expectedUrl = "http://localhost/contents?transaction_id=123";
        ContentRequest request = getContentRequest().get(0);
        apiService.createOrUpdateContent(TRANSACTION_ID, request, API_KEY);
        verify(httpClient, times(1)).postForLocation(urlCaptor.capture(), entityListRequestCaptor.capture(), anyMap());
        assertEquals(expectedUrl, urlCaptor.getValue());
        EntityListRequest<ContentRequest> requestValue = entityListRequestCaptor.getValue();
        List<ContentRequest> items = requestValue.getItems();
        assertEquals(request, items.get(0));
    }

    @Test
    public void getCustomers() {
        String expectedUrl = "http://localhost/customers?bizId=123&source_system_type=NET_SUITE&open_balances_only=true";
        CustomerResponse customerResponse = CustomerResponse.builder().id(9900L).build();
        CustomerResponse[] responses = Collections.singleton(customerResponse).toArray(new CustomerResponse[]{});
        when(httpClient.get(urlCaptor.capture(), any(Class.class), anyMap())).thenReturn(responses);

        Collection<CustomerResponse> customers = apiService.getCustomers(BIZ_ID, API_KEY, SOURCE_SYSTEM_TYPE, true);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(customers.iterator().next(), customerResponse);
    }

    @Test
    public void deleteCustomers() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/customers/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), any(DeleteEntityRequest.class), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        apiService.deleteCustomers(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());

    }

    @Test
    public void deleteInvoices() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/invoices/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        Set<String> response = apiService.deleteInvoices(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void deletePayments() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/payments/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        Set<String> response = apiService.deletePayments(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void deleteCreditMemos() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/credit-memo/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        Set<String> response = apiService.deleteCreditMemos(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void deleteAdjustments() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/adjustments/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});
        Set<String> response = apiService.deleteAdjustments(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void deleteContacts() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/contacts/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        Set<String> response = apiService.deleteContacts(getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void deleteEntity() throws ExecutionException, InterruptedException {
        String expectedUrl = "http://localhost/contacts/delete?transaction_id=123";
        when(httpClient.post(urlCaptor.capture(), deleteEntityRequestArgumentCaptor.capture(), any(Class.class), anyMap())).thenReturn(new String[]{DELETE_RESPONSE});

        Set<String> response = apiService.deleteEntity(SyncEntity.CONTACTS, getDeleteEntities(), TRANSACTION_ID, API_KEY);
        assertEquals(expectedUrl, urlCaptor.getValue());
        assertEquals(DELETE_RESPONSE, response.iterator().next());
        DeleteEntityRequest value = deleteEntityRequestArgumentCaptor.getValue();
        DeleteEntity deleteEntity = value.getDeletedEntities().get(0);
        assertEquals(INTERNAL_ID, deleteEntity.getIntegration().getInternalId());
    }

    @Test
    public void getActiveEntityIds() {
        String expectedId = "http://localhost/credit-memo/active?bizId=123&sourceSystemType=NET_SUITE";
        when(httpClient.get(urlCaptor.capture(), eq(String[].class), anyMap())).thenReturn(new String[]{SOME_ID});
        List<String> activeEntityIds = apiService.getActiveEntityIds(SyncEntity.CM, BIZ_ID, API_KEY, SOURCE_SYSTEM_TYPE);
        assertEquals(expectedId, urlCaptor.getValue());
        assertEquals(activeEntityIds.get(0), SOME_ID);
    }

    @Test
    public void testMessages(){
        String expectedUrl = "http://localhost/messages";
        apiService.sendLoggingMessage(API_KEY, loggingMessage);

        verify(httpClient, times(1)).postForLocation(eq(expectedUrl), eq(loggingMessage), anyMap());
    }

    private Date getStartDate() {
        return dateFromString("1970-01-01");
    }

    private Date getEndDate() {
        return dateFromString("2018-01-01");
    }

    private Date dateFromString(String date) {
        String pattern = "yyyy-MM-dd";
        try {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<InvoiceRequest> getInvoices() {
        ArrayList<InvoiceRequest> invoiceRequests = new ArrayList<>();
        Integration integration = buildIntagration();
        InvoiceRequest invoiceRequest = InvoiceRequest.builder().integration(integration).build();
        invoiceRequests.add(invoiceRequest);
        return invoiceRequests;
    }

    private List<DeleteEntity> getDeleteEntities() {
        ArrayList<DeleteEntity> deleteEntityRequests = new ArrayList<>();
        deleteEntityRequests.add(DeleteEntity.builder().integration(buildIntagration()).build());
        return deleteEntityRequests;
    }

    private List<ContactRequest> getContacts() {
        ArrayList<ContactRequest> contactRequests = new ArrayList<>();
        contactRequests.add(ContactRequest.builder().integration(buildIntagration()).build());
        return contactRequests;
    }

    private List<CustomerRequest> getCustomersList() {
        ArrayList<CustomerRequest> customerRequests = new ArrayList<>();
        customerRequests.add(CustomerRequest.builder().integration(buildIntagration()).build());
        return customerRequests;
    }

    private List<PaymentRequest> getPaymentRequest() {
        ArrayList<PaymentRequest> paymentRequests = new ArrayList<>();
        paymentRequests.add(PaymentRequest.builder().integration(buildIntagration()).build());
        return paymentRequests;
    }

    private List<CreditMemoRequest> getCreditMemos() {
        ArrayList<CreditMemoRequest> creditMemos = new ArrayList<>();
        creditMemos.add(CreditMemoRequest.builder().integration(buildIntagration()).build());
        return creditMemos;
    }

    private List<AdjustmentRequest> getAdjustments() {
        ArrayList<AdjustmentRequest> adjustmentRequests = new ArrayList<>();
        adjustmentRequests.add(AdjustmentRequest.builder().integration(buildIntagration()).build());
        return adjustmentRequests;
    }

    private List<ExternalCompanyRequest> getExternalCompanyRequest() {
        ArrayList<ExternalCompanyRequest> externalCompanyRequests = new ArrayList<>();
        externalCompanyRequests.add(ExternalCompanyRequest.builder().integration(buildIntagration()).build());
        return externalCompanyRequests;
    }

    private List<ExternalContactRequest> getExternalContacts() {
        ArrayList<ExternalContactRequest> externalContactRequests = new ArrayList<>();
        externalContactRequests.add(ExternalContactRequest.builder().integration(buildIntagration()).build());
        return externalContactRequests;
    }

    private List<SalesRequest> getSalesRequest() {
        ArrayList<SalesRequest> salesRequests = new ArrayList<>();
        salesRequests.add(SalesRequest.builder().integration(buildIntagration()).build());
        return salesRequests;
    }

    private List<CurrencyRequest> getCurrencyRequest() {
        ArrayList<CurrencyRequest> currencyRequests = new ArrayList<>();
        currencyRequests.add(CurrencyRequest.builder().integration(buildIntagration()).build());
        return currencyRequests;
    }

    private List<ContentRequest> getContentRequest() {
        ArrayList<ContentRequest> contentRequests = new ArrayList<>();
        contentRequests.add(ContentRequest.builder().integration(buildIntagration()).build());
        return contentRequests;
    }

    private Integration buildIntagration() {
        return Integration.builder().internalId(INTERNAL_ID).source(SOURCE_SYSTEM_TYPE).build();
    }
}
package com.yaypay.integration.service.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaypay.exception.HttpClientCallException;
import com.yaypay.exception.HttpClientNotSuccessStatusException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

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

public class HttpClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient client;

    public HttpClient() {
        this.objectMapper = ObjectMapperBuilder.integrationObjectMapper();
        this.client = configureHttpClient();
    }

    private CloseableHttpClient configureHttpClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(10);
        connManager.setDefaultMaxPerRoute(10);
        connManager.closeIdleConnections(5, TimeUnit.SECONDS);
        connManager.closeExpiredConnections();

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout((int) TimeUnit.MINUTES.toMillis(90))
                .setConnectTimeout((int) TimeUnit.MINUTES.toMillis(90))
                .setConnectionRequestTimeout((int) TimeUnit.MINUTES.toMillis(30))
                .build();
        return HttpClientBuilder
                .create()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public <T> T get(String url, Class<T> resultClass) {
        return get(url, resultClass, Collections.emptyMap());
    }

    public <T> T get(String url, Class<T> resultClass, Map<String, String> additionalHeaders) {
        HttpGet httpGet = new HttpGet(url);
        return makeCall(url, resultClass, httpGet, additionalHeaders);
    }

    public <T> T post(String url, Object body, Class<T> resultClass) {
        return post(url, body, resultClass, Collections.emptyMap());
    }

    public <T> T post(String url, Object body, Class<T> resultClass, Map<String, String> additionalHeaders) {
        HttpPost httpPost = new HttpPost(url);
        prepareRequestBody(url, body, httpPost);
        return makeCall(url, resultClass, httpPost, additionalHeaders);
    }

    public void postForLocation(String url, Object body, Map<String, String> additionalHeaders) {
        HttpPost httpPost = new HttpPost(url);
        prepareRequestBody(url, body, httpPost);
        makeCallForLocation(url, httpPost, additionalHeaders);
    }

    public <T> T put(String url, Object body, Class<T> resultClass) {
        return put(url, body, resultClass, Collections.emptyMap());
    }

    public <T> T put(String url, Object body, Class<T> resultClass, Map<String, String> additionalHeaders) {
        HttpPut httpPost = new HttpPut(url);
        prepareRequestBody(url, body, httpPost);
        return makeCall(url, resultClass, httpPost, additionalHeaders);
    }

    public void delete(String url) {
        delete(url, Collections.emptyMap());
    }

    public void delete(String url, Map<String, String> additionalHeaders) {
        HttpDelete httpPost = new HttpDelete(url);
        makeCall(url, Void.class, httpPost, additionalHeaders);
    }

    private void prepareRequestBody(String url, Object body, HttpEntityEnclosingRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            request.setEntity(entity);
        } catch (IOException e) {
            throw new HttpClientCallException(String.format("Error during prepare http request for url %s", url), e);
        }
    }

    private <T> T makeCall(String url, Class<T> resultClass, HttpUriRequest request, Map<String, String> additionalHeaders) {
        fillHeaders(request, additionalHeaders);
        try (CloseableHttpResponse response = client.execute(request)) {
            verifyStatus(url, response);
            return objectMapper.readValue(response.getEntity().getContent(), resultClass);
        } catch (IOException e) {
            String message = String.format("Error during call %s", url);
            logger.error(message);
            throw new HttpClientCallException(message, e);
        }
    }

    private void makeCallForLocation(String url, HttpUriRequest request, Map<String, String> additionalHeaders) {
        fillHeaders(request, additionalHeaders);
        try (CloseableHttpResponse response = client.execute(request)) {
            verifyStatus(url, response);
        } catch (IOException e) {
            String message = String.format("Error during call %s", url);
            logger.error(message);
            throw new HttpClientCallException(message, e);
        }
    }

    private boolean isSuccessStatus(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return HttpStatus.SC_OK == statusCode ||
                HttpStatus.SC_CREATED == statusCode ||
                HttpStatus.SC_ACCEPTED == statusCode ||
                HttpStatus.SC_NO_CONTENT == statusCode;
    }

    private void verifyStatus(String url, HttpResponse response) {
        if (!isSuccessStatus(response)) {
            String message = String.format("Received not success status (%s) for url [%s]", response.getStatusLine().getStatusCode(), url);
            logger.error(message);
            throw new HttpClientNotSuccessStatusException(message);
        }
    }

    private void fillHeaders(HttpUriRequest request, Map<String, String> additionalHeaders) {
        request.addHeader(ACCEPT, "application/json;charset=UTF-8");
        request.addHeader(CONTENT_TYPE, "application/json;charset=UTF-8");

        if (!isEmpty(additionalHeaders)) {
            for (Map.Entry<String, String> stringStringEntry : additionalHeaders.entrySet()) {
                request.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
    }
    @Override
    public void close() throws IOException {
        client.close();
    }
}

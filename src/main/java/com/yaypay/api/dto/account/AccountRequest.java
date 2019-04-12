/*
 * *************************************************************************
 * * Yaypay CONFIDENTIAL   2019
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 */

package com.yaypay.api.dto.account;

import com.yaypay.api.dto.core.EntityRequest;
import com.yaypay.api.dto.core.Integration;

/*
 * Author : Andrii Kuzovchikov
 * Date Created: 2019/02/27
 */
public class AccountRequest extends EntityRequest {
    private Account account;
    private Integration integration;

    @java.beans.ConstructorProperties({"account", "integration"})
    AccountRequest(Account account, Integration integration) {
        this.account = account;
        this.integration = integration;
    }

    public static AccountRequest.AccountRequestBuilder builder() {
        return new AccountRequest.AccountRequestBuilder();
    }

    @Override
    public Object getEntity() {
        return account;
    }

    public Account getAccount() {
        return this.account;
    }

    public Integration getIntegration() {
        return this.integration;
    }

    public static class AccountRequestBuilder {
        private Account account;
        private Integration integration;

        AccountRequestBuilder() {
        }

        public AccountRequest.AccountRequestBuilder account(Account account) {
            this.account = account;
            return this;
        }

        public AccountRequest.AccountRequestBuilder integration(Integration integration) {
            this.integration = integration;
            return this;
        }

        public AccountRequest build() {
            return new AccountRequest(account, integration);
        }

        public String toString() {
            return "AccountRequest.AccountRequestBuilder(account=" + this.account + ", integration=" + this.integration + ")";
        }
    }
}

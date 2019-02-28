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

/*
 * Author : Andrii Kuzovchikov
 * Date Created: 2019/02/27
 */
public class Account {
    private String name;
    private String accountNumber;
    private String accountType;

    @java.beans.ConstructorProperties({"name", "accountNumber", "accountType"})
    Account(String name, String accountNumber, String accountType) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public static Account.AccountBuilder builder() {
        return new Account.AccountBuilder();
    }

    public String getName() {
        return this.name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public static class AccountBuilder {
        private String name;
        private String accountNumber;
        private String accountType;

        AccountBuilder() {
        }

        public Account.AccountBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Account.AccountBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Account.AccountBuilder accountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public Account build() {
            return new Account(name, accountNumber, accountType);
        }

        public String toString() {
            return "Account.AccountBuilder(name=" + this.name + ", accountNumber=" + this.accountNumber + ", accountType=" + this.accountType + ")";
        }
    }
}

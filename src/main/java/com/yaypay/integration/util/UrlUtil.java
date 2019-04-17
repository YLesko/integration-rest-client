/*
 * *************************************************************************
 * * Yaypay CONFIDENTIAL   2018
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 */

package com.yaypay.integration.util;

import com.yaypay.api.dto.log.SyncEntity;
import com.yaypay.exception.SyncEntityUnknownException;

/*
 * Author : Andrii Kuzovchikov
 * Date Created: 2018/10/10
 */
public class UrlUtil {

    private UrlUtil() { }

    public static String getUrlPrefx(SyncEntity syncEntityType) {
        switch (syncEntityType) {
            case CUSTOMERS:
                return "customers";
            case ACCOUNTS:
                return "accounts";
            case INVOICES:
                return "invoices";
            case PAYMENTS:
                return "payments";
            case CM:
                return "credit-memo";
            case ADJUSTMENTS:
                return "adjustments";
            case CONTACTS:
                return "contacts";
            default:
                throw new SyncEntityUnknownException("Unknown SyncEntity enum type: " + syncEntityType.toString());
        }
    }
}

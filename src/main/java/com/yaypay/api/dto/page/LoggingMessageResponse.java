/*
 * *************************************************************************
 * * Yaypay CONFIDENTIAL   2019
 * * All Rights Reserved. * *
 * NOTICE: All information contained herein is, and remains the property of Yaypay Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained  herein are proprietary to Yaypay Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material  is strictly forbidden unless prior written permission is obtained  from Yaypay Incorporated.
 */

package com.yaypay.api.dto.page;

import com.yaypay.api.dto.log.LoggingMessageFull;

import java.util.List;

/*
 * Author : Andrii Kuzovchikov
 * Date Created: 2019/05/01
 */
public class LoggingMessageResponse {
    private List<LoggingMessageFull> content;
    private Pagination page;

    public List<LoggingMessageFull> getContent() {
        return content;
    }

    public void setContent(List<LoggingMessageFull> content) {
        this.content = content;
    }

    public Pagination getPage() {
        return page;
    }

    public void setPage(Pagination page) {
        this.page = page;
    }
}

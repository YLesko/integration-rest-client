package com.yaypay.api.dto.external_note;

import com.yaypay.api.dto.core.EntityRequest;
import com.yaypay.api.dto.core.Integration;

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

public class ExternalNoteRequest extends EntityRequest {
    private ExternalNote externalNote;
    private Integration integration;

    @java.beans.ConstructorProperties({"externalNote", "integration"})
    ExternalNoteRequest(ExternalNote externalNote, Integration integration) {
        this.externalNote = externalNote;
        this.integration = integration;
    }

    public static ExternalNoteRequestBuilder builder() {
        return new ExternalNoteRequestBuilder();
    }

    @Override
    public Object getEntity() {
        return externalNote;
    }

    public ExternalNote getExternalNote() {
        return this.externalNote;
    }

    public Integration getIntegration() {
        return this.integration;
    }

    public static class ExternalNoteRequestBuilder {
        private ExternalNote externalNote;
        private Integration integration;

        ExternalNoteRequestBuilder() {
        }

        public ExternalNoteRequest.ExternalNoteRequestBuilder externalNote(ExternalNote externalNote) {
            this.externalNote = externalNote;
            return this;
        }

        public ExternalNoteRequest.ExternalNoteRequestBuilder integration(Integration integration) {
            this.integration = integration;
            return this;
        }

        public ExternalNoteRequest build() {
            return new ExternalNoteRequest(externalNote, integration);
        }

        public String toString() {
            return "ExternalNoteRequest.ExternalNoteRequestBuilder(externalNote=" + this.externalNote + ", integration=" + this.integration + ")";
        }
    }
}

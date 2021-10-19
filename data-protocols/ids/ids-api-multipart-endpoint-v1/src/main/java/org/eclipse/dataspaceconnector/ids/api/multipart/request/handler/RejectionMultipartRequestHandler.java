/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.api.multipart.request.handler;

import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RequestMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RejectionMultipartRequestHandler implements MultipartRequestHandler {
    private final MessageFactory messageFactory;

    public RejectionMultipartRequestHandler(MessageFactory messageFactory) {
        Objects.requireNonNull(messageFactory);

        this.messageFactory = messageFactory;
    }

    @Override
    public boolean canHandle(MultipartRequest multipartRequest) {
        return true;
    }

    @Override
    public @NotNull MultipartResponse handleRequest(MultipartRequest multipartRequest) {
        RequestMessage requestMessage = multipartRequest.getHeader();
        RejectionMessage rejectionMessage = messageFactory.createRejectionMessage(requestMessage);

        return MultipartResponse.Builder.newInstance()
                .header(rejectionMessage)
                .build();
    }
}

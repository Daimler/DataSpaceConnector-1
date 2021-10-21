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

package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RequestMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// TODO Remove this class
public class RejectionMultipartRequestHandler implements MultipartRequestHandler {
    private final RejectionMessageFactory rejectionMessageFactory;

    public RejectionMultipartRequestHandler(RejectionMessageFactory rejectionMessageFactory) {
        this.rejectionMessageFactory = Objects.requireNonNull(rejectionMessageFactory);
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        return true;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {
        RequestMessage requestMessage = multipartRequest.getHeader();
        RejectionMessage rejectionMessage = rejectionMessageFactory.createRejectionMessage(requestMessage);

        return MultipartResponse.Builder.newInstance()
                .header(rejectionMessage)
                .build();
    }
}

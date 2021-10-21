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

package org.eclipse.dataspaceconnector.ids.api.multipart.controller;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.Token;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.MultipartRequestHandlerResolver;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.MultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.RejectionMultipartRequestHandler;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

// TODO Add Integration Test with real request

@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.MULTIPART_FORM_DATA })
@Path(MultipartController.PATH)
public class MultipartController {
    public static final String PATH = "/ids/multipart";
    private static final String HEADER = "header";
    private static final String PAYLOAD = "payload";

    private final Monitor monitor;
    private final IdentityService identityService;
    private final MultipartRequestHandlerResolver multipartRequestHandlerResolver;
    private final RejectionMultipartRequestHandler rejectionMultipartRequestHandler;

    public MultipartController(
            Monitor monitor,
            IdentityService identityService,
            MultipartRequestHandlerResolver multipartRequestHandlerResolver,
            RejectionMultipartRequestHandler rejectionMultipartRequestHandler) {
        this.monitor = monitor;
        this.identityService = identityService;
        this.multipartRequestHandlerResolver = multipartRequestHandlerResolver;
        this.rejectionMultipartRequestHandler = rejectionMultipartRequestHandler;
    }

    // TODO Add self description response on GET root path

    @POST
    public Response request(
            @FormDataParam("header") RequestMessage header,
            @FormDataParam("payload") String payload) {
        if (header == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Token token = header.getAuthorizationToken();
        if (token == null || token.getTokenValue() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        VerificationResult verificationResult = identityService.verifyJwtToken(token.getTokenValue(), null);
        if (verificationResult == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!verificationResult.valid()) {
            String error = verificationResult.error();
            if (error != null) {
                monitor.warning(String.format("Invalid authentication attempt: %s", verificationResult.error()));
            } else {
                monitor.warning("Invalid authentication attempt");
            }

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MultipartRequest multipartRequest = MultipartRequest.Builder.newInstance()
                .header(header)
                .payload(payload)
                .build();

        MultipartResponse multipartResponse = null;
        MultipartRequestHandler multipartRequestHandler = multipartRequestHandlerResolver.resolveHandler(multipartRequest);
        if (multipartRequestHandler != null) {
            multipartResponse = multipartRequestHandler.handleRequest(multipartRequest);
        }

        if (multipartResponse == null) {
            multipartResponse = rejectionMultipartRequestHandler.handleRequest(multipartRequest);
        }

        FormDataMultiPart multiPart = new FormDataMultiPart();
        if (multipartResponse != null) {
            Message responseHeader = multipartResponse.getHeader();
            if (responseHeader != null) {
                multiPart.bodyPart(new FormDataBodyPart(HEADER, responseHeader, MediaType.APPLICATION_JSON_TYPE));
            }

            Object responsePayload = multipartResponse.getPayload();
            if (responsePayload != null) {
                multiPart.bodyPart(new FormDataBodyPart(PAYLOAD, responsePayload, MediaType.APPLICATION_JSON_TYPE));
            }
        }

        return Response.ok(multiPart).build();
    }
}

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

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.RequestMessage;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.RequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.util.List;

import static org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageUtil.messageTypeNotSupported;
import static org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageUtil.notAuthenticated;
import static org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageUtil.notAuthorized;
import static org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageUtil.notFound;

@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.MULTIPART_FORM_DATA })
@Path(MultipartController.PATH)
public class MultipartController {
    public static final String PATH = "/ids/multipart";
    private static final String HEADER = "header";
    private static final String PAYLOAD = "payload";

    private final Monitor monitor;
    private final IdentityService identityService;
    private final List<RequestHandler> multipartRequestHandlers;
    private final MultipartControllerSettings multipartControllerSettings;

    public MultipartController(
            MultipartControllerSettings multipartControllerSettings,
            Monitor monitor,
            IdentityService identityService,
            List<RequestHandler> multipartRequestHandlers) {
        this.monitor = monitor;
        this.identityService = identityService;
        this.multipartRequestHandlers = multipartRequestHandlers;
        this.multipartControllerSettings = multipartControllerSettings;
    }

    @POST
    public Response request(
            @FormDataParam("header") RequestMessage header,
            @FormDataParam("payload") String payload) {
        if (header == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        DynamicAttributeToken dynamicAttributeToken = header.getSecurityToken();
        if (dynamicAttributeToken == null || dynamicAttributeToken.getTokenValue() == null) {
            return Response.ok(
                    createFormDataMultiPart(
                            notAuthenticated(header, multipartControllerSettings.getId()), null)).build();
        }

        VerificationResult verificationResult = identityService.verifyJwtToken(
                dynamicAttributeToken.getTokenValue(), null);
        if (verificationResult == null) {
            return Response.ok(
                    createFormDataMultiPart(
                            notAuthenticated(header, multipartControllerSettings.getId()), null)).build();
        }

        if (!verificationResult.valid()) {
            return Response.ok(
                    createFormDataMultiPart(
                            notAuthorized(header, multipartControllerSettings.getId()), null)).build();
        }

        MultipartRequest multipartRequest = MultipartRequest.Builder.newInstance()
                .header(header)
                .payload(payload)
                .verificationResult(verificationResult)
                .build();

        RequestHandler requestHandler = getRequestHandler(multipartRequest);
        if (requestHandler == null) {
            return Response.ok(
                    createFormDataMultiPart(
                            messageTypeNotSupported(header, multipartControllerSettings.getId()), null)).build();
        }

        MultipartResponse multipartResponse = requestHandler.handleRequest(multipartRequest);
        if (multipartResponse != null) {
            return Response.ok(
                    createFormDataMultiPart(multipartResponse)).build();
        }

        return Response.ok(
                createFormDataMultiPart(
                        notFound(header, multipartControllerSettings.getId()), null)).build();
    }

    private FormDataMultiPart createFormDataMultiPart(MultipartResponse multipartResponse) {
        return createFormDataMultiPart(multipartResponse.getHeader(), multipartResponse.getPayload());
    }

    private FormDataMultiPart createFormDataMultiPart(Object header, Object payload) {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        if (header != null) {
            multiPart.bodyPart(new FormDataBodyPart(HEADER, header, MediaType.APPLICATION_JSON_TYPE));
        }

        if (payload != null) {
            multiPart.bodyPart(new FormDataBodyPart(PAYLOAD, payload, MediaType.APPLICATION_JSON_TYPE));
        }

        return multiPart;
    }

    private RequestHandler getRequestHandler(MultipartRequest multipartRequest) {
        for (RequestHandler multipartRequestHandler : multipartRequestHandlers) {
            if (multipartRequestHandler.canHandle(multipartRequest)) {
                return multipartRequestHandler;
            }
        }

        return null;
    }
}

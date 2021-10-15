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

import de.fraunhofer.iais.eis.RequestMessage;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.MultipartRequestHandlerResolver;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.MultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.RejectionMultipartRequestHandler;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.util.Optional;

@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.MULTIPART_FORM_DATA })
@Path(MultipartController.PATH)
public class MultipartController {
    public static final String PATH = "/ids/multipart";
    private static final String HEADER = "header";
    private static final String PAYLOAD = "payload";

    private final MultipartRequestHandlerResolver multipartRequestHandlerResolver;
    private final RejectionMultipartRequestHandler rejectionMultipartRequestHandler;

    public MultipartController(
            MultipartRequestHandlerResolver multipartRequestHandlerResolver,
            RejectionMultipartRequestHandler rejectionMultipartRequestHandler) {
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

        MultipartRequest multipartRequest = MultipartRequest.Builder.newInstance()
                .header(header)
                .payload(payload)
                .build();

        MultipartRequestHandler multipartRequestHandler = multipartRequestHandlerResolver
                .resolveHandler(multipartRequest)
                .orElse(rejectionMultipartRequestHandler);

        MultipartResponse multipartResponse = multipartRequestHandler.handleRequest(multipartRequest);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        Optional.ofNullable(multipartResponse.getHeader())
                .ifPresent(entity -> multiPart.bodyPart(new FormDataBodyPart(HEADER, entity, MediaType.APPLICATION_JSON_TYPE)));
        Optional.ofNullable(multipartResponse.getPayload())
                .ifPresent(entity -> multiPart.bodyPart(new FormDataBodyPart(PAYLOAD, entity, MediaType.APPLICATION_JSON_TYPE)));

        return Response.ok(multiPart).build();
    }
}

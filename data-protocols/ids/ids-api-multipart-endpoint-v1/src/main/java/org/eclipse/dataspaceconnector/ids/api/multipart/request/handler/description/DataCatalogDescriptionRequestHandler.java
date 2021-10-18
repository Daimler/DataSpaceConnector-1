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

package org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description;

import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.DataCatalogService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.net.URI;

public class DataCatalogDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final DataCatalogService dataCatalogService;

    public DataCatalogDescriptionRequestHandler(
            MessageFactory messageFactory,
            DataCatalogService dataCatalogService) {
        this.messageFactory = messageFactory;
        this.dataCatalogService = dataCatalogService;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return null;
        }

        IdsId ids = IdsId.fromUri(uri);
        if (ids.getType() != IdsId.Type.CATALOG) {
            return null;
        }

        // return data catalog
        DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);
        Catalog dataCatalog = dataCatalogService.createDataCatalog();
        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(dataCatalog)
                .build();
    }
}

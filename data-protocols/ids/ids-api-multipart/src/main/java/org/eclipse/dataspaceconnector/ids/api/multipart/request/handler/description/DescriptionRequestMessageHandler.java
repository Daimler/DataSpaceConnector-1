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

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;

public interface DescriptionRequestMessageHandler {

    MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload);
}

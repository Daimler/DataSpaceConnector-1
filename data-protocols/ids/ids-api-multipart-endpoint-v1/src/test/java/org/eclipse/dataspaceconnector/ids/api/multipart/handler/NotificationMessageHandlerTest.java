/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.ParticipantCertificateGrantedMessageBuilder;
import de.fraunhofer.iais.eis.ParticipantUpdateMessage;
import de.fraunhofer.iais.eis.ParticipantUpdateMessageBuilder;
import de.fraunhofer.iais.eis.RejectionMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.ids.IdsResponseMessageFactory;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationMessageHandlerTest {

    private Handler subHandler;
    private NotificationMessageHandlerRegistry subHandlers;
    private NotificationMessageHandler handler;
    private IdsResponseMessageFactory responseMessageFactory;

    @BeforeEach
    public void setUp() {
        subHandlers = new NotificationMessageHandlerRegistry();
        subHandler = Mockito.mock(Handler.class);
        responseMessageFactory = Mockito.mock(IdsResponseMessageFactory.class);
        subHandlers.addHandler(ParticipantUpdateMessage.class, subHandler);
        handler = new NotificationMessageHandler(responseMessageFactory, subHandlers);
    }

    @Test
    void canHandle_noSubHandlerForMessage_shouldReturnFalse() {
        var request = createMultipartRequest(new ArtifactRequestMessageBuilder().build());
        assertThat(handler.canHandle(request)).isFalse();
    }

    @Test
    void canHandle_supportedMessage_shouldReturnTrue() {
        var request = createMultipartRequest(new ParticipantUpdateMessageBuilder().build());
        assertThat(handler.canHandle(request)).isTrue();
    }

    @Test
    void delegateToSubHandler_subHandlerNotFound_shouldReturnRejectionMessage() {
        when(responseMessageFactory.createMessageTypeNotSupportedMessage(Mockito.any())).thenReturn(mock(RejectionMessage.class));

        var request = createMultipartRequest(new ParticipantCertificateGrantedMessageBuilder().build());

        var response = handler.handleRequest(request, createClaimToken());

        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getHeader()).isInstanceOf(RejectionMessage.class);
                });
    }

    @Test
    void delegateToSubHandler_shouldReturnResultFromSubHandler() {
        var request = createMultipartRequest(new ParticipantUpdateMessageBuilder().build());
        var verificationResult = createClaimToken();
        var subHandlerResponse = MultipartResponse.Builder.newInstance().header(new MessageProcessedNotificationMessageBuilder().build()).build();
        when(subHandler.handleRequest(request, verificationResult)).thenReturn(subHandlerResponse);

        var response = handler.handleRequest(request, verificationResult);

        verify(subHandler, Mockito.times(1)).handleRequest(request, verificationResult);
        assertThat(response)
                .isNotNull()
                .isEqualTo(subHandlerResponse);
    }

    private static MultipartRequest createMultipartRequest(Message message) {
        return MultipartRequest.Builder.newInstance().header(message).build();
    }

    private static ClaimToken createClaimToken() {
        return ClaimToken.Builder.newInstance().build();
    }
}
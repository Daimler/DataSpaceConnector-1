package org.eclipse.dataspaceconnector.ids.api.multipart.dispatcher.sender;

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdsMultipartSenderTest {
    private final IdentityService identityService = mock(IdentityService.class);

    @Test
    void should_fail_if_token_retrieval_fails() {
        when(identityService.obtainClientCredentials("remoteConnectorId")).thenReturn(Result.failure("error"));
        var sender = new TestIdsMultipartSender("any", "https://localhost", mock(OkHttpClient.class), new Serializer(), mock(Monitor.class), identityService, mock(TransformerRegistry.class));

        var result = sender.send(new TestRemoteMessage(), () -> "any");

        assertThat(result).failsWithin(1, TimeUnit.SECONDS);
    }

    private static class TestIdsMultipartSender extends IdsMultipartSender<TestRemoteMessage, Object> {

        protected TestIdsMultipartSender(String connectorId, String idsWebhookAddress, OkHttpClient httpClient, Serializer serializer,
                                         Monitor monitor, IdentityService identityService, TransformerRegistry transformerRegistry) {
            super(connectorId, idsWebhookAddress, httpClient, monitor, identityService, transformerRegistry, serializer);
        }

        @Override
        public Class<TestRemoteMessage> messageType() {
            return null;
        }

        @Override
        protected String retrieveRemoteConnectorId(TestRemoteMessage request) {
            return "remoteConnectorId";
        }

        @Override
        protected String retrieveRemoteConnectorAddress(TestRemoteMessage request) {
            return null;
        }

        @Override
        protected Message buildMessageHeader(TestRemoteMessage request, DynamicAttributeToken token) throws Exception {
            return null;
        }

        @Override
        protected Object getResponseContent(IdsMultipartParts parts) throws Exception {
            return null;
        }
    }

    private static class TestRemoteMessage implements RemoteMessage {

        @Override
        public String getProtocol() {
            return null;
        }
    }
}
package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import org.eclipse.dataspaceconnector.ids.core.util.CalendarUtil;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class RejectionMessageFactory {
    private final ConfigurationProvider configurationProvider;
    private final IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider;

    public RejectionMessageFactory(ConfigurationProvider configurationProvider,
                                   IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider) {
        this.configurationProvider = configurationProvider;
        this.outboundProtocolVersionProvider = outboundProtocolVersionProvider;
    }

    public RejectionMessage createRejectionMessage(Message correlationMessage) {
        IdsId messageId = IdsId.message(UUID.randomUUID().toString());

        RejectionMessageBuilder builder = new RejectionMessageBuilder(messageId.toUri());

        builder._contentVersion_(outboundProtocolVersionProvider.getIdsProtocolVersion().getValue());
        builder._modelVersion_(outboundProtocolVersionProvider.getIdsProtocolVersion().getValue());

        URI connectorId = configurationProvider.resolveId();
        if (connectorId != null) {
            builder._issuerConnector_(connectorId);
            builder._senderAgent_(connectorId);
        }

        builder._issued_(CalendarUtil.gregorianNow());

        if (correlationMessage != null) {
            URI correlationMessageId = correlationMessage.getId();
            if (correlationMessageId != null) {
                builder._correlationMessage_(correlationMessageId);
            }

            URI senderAgent = correlationMessage.getSenderAgent();
            if (senderAgent != null) {
                builder._recipientAgent_(new ArrayList<>(Collections.singletonList(senderAgent)));
            }

            URI issuerConnector = correlationMessage.getIssuerConnector();
            if (issuerConnector != null) {
                builder._recipientConnector_(new ArrayList<>(Collections.singletonList(issuerConnector)));
            }
        }
        return builder.build();
    }
}

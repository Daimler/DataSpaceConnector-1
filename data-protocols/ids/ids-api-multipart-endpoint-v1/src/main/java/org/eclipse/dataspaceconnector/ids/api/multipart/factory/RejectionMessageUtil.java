package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import org.eclipse.dataspaceconnector.ids.core.util.CalendarUtil;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocol;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Deprecated // This functionality will be moved to a transformer class
public final class RejectionMessageUtil {

    private RejectionMessageUtil() {
    }

    public static RejectionMessage notFound(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.NOT_FOUND)
                .build();
    }

    public static RejectionMessage notAuthenticated(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.NOT_AUTHENTICATED)
                .build();
    }

    public static RejectionMessage notAuthorized(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.NOT_AUTHORIZED)
                .build();
    }

    public static RejectionMessage tooManyResults(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.TOO_MANY_RESULTS)
                .build();
    }

    public static RejectionMessage malformedMessage(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.MALFORMED_MESSAGE)
                .build();
    }

    public static RejectionMessage internalRecipientError(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.INTERNAL_RECIPIENT_ERROR)
                .build();
    }

    public static RejectionMessage methodNotSupported(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.METHOD_NOT_SUPPORTED)
                .build();
    }

    public static RejectionMessage messageTypeNotSupported(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.MESSAGE_TYPE_NOT_SUPPORTED)
                .build();
    }

    public static RejectionMessage versionNotSupported(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.VERSION_NOT_SUPPORTED)
                .build();
    }

    public static RejectionMessage badParameters(URI connectorId) {
        return badParameters(null, connectorId);
    }

    public static RejectionMessage badParameters(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.BAD_PARAMETERS)
                .build();
    }

    public static RejectionMessage temporarilyNotAvailable(Message correlationMessage, URI connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.TEMPORARILY_NOT_AVAILABLE)
                .build();
    }

    private static RejectionMessageBuilder createRejectionMessageBuilder(Message correlationMessage, URI connectorId) {
        IdsId messageId = IdsId.message(UUID.randomUUID().toString());

        RejectionMessageBuilder builder = new RejectionMessageBuilder(messageId.toUri());

        builder._contentVersion_(IdsProtocol.INFORMATION_MODEL_VERSION);
        builder._modelVersion_(IdsProtocol.INFORMATION_MODEL_VERSION);

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

        return builder;
    }
}

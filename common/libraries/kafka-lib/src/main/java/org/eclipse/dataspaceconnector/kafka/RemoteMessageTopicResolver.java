package org.eclipse.dataspaceconnector.kafka;

import org.eclipse.dataspaceconnector.spi.message.MessageContext;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;

public interface RemoteMessageTopicResolver {

    String resolveTopic(RemoteMessage remoteMessage, MessageContext context);
}

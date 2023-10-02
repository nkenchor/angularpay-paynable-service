package io.angularpay.paynable.ports.inbound;

import io.angularpay.paynable.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}

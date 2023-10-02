package io.angularpay.paynable.adapters.inbound;

import io.angularpay.paynable.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.paynable.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.paynable.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.paynable.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}

package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;

import java.util.Objects;

public interface UpdatesPublisherCommand<T extends PaynableRequestSupplier> {

    RedisAdapter getRedisAdapter();

    String convertToUpdatesMessage(PaynableRequest paynableRequest) throws JsonProcessingException;

    default void publishUpdates(T t) {
        PaynableRequest paynableRequest = t.getPaynableRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        if (Objects.nonNull(paynableRequest) && Objects.nonNull(redisAdapter)) {
            try {
                String message = this.convertToUpdatesMessage(paynableRequest);
                redisAdapter.publishUpdates(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}

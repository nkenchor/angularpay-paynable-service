package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;

import java.util.Objects;

public interface TTLPublisherCommand<T extends PaynableRequestSupplier> {

    RedisAdapter getRedisAdapter();

    String convertToTTLMessage(PaynableRequest paynableRequest, T t) throws JsonProcessingException;

    default void publishTTL(T t) {
        PaynableRequest paynableRequest = t.getPaynableRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        if (Objects.nonNull(paynableRequest) && Objects.nonNull(redisAdapter)) {
            try {
                String message = this.convertToTTLMessage(paynableRequest, t);
                redisAdapter.publishTTL(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}

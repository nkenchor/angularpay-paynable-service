package io.angularpay.paynable.helpers;

import io.angularpay.paynable.domain.Bargain;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.RequestStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import static io.angularpay.paynable.common.Constants.SERVICE_CODE;
import static io.angularpay.paynable.util.SequenceGenerator.generateRequestTag;

public class ObjectFactory {

    public static PaynableRequest paynableRequestWithDefaults() {
        return PaynableRequest.builder()
                .reference(UUID.randomUUID().toString())
                .serviceCode(SERVICE_CODE)
                .verified(false)
                .status(RequestStatus.ACTIVE)
                .requestTag(generateRequestTag())
                .investors(new ArrayList<>())
                .bargain(Bargain.builder()
                        .offers(new ArrayList<>())
                        .build())
                .build();
    }
}
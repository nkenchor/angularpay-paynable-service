package io.angularpay.paynable.domain.commands;

import io.angularpay.paynable.domain.PaynableRequest;

public interface PaynableRequestSupplier {
    PaynableRequest getPaynableRequest();
}

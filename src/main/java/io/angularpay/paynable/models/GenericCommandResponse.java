
package io.angularpay.paynable.models;

import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.commands.PaynableRequestSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GenericCommandResponse extends GenericReferenceResponse implements PaynableRequestSupplier {

    private final String requestReference;
    private final String itemReference;
    private final PaynableRequest paynableRequest;
}

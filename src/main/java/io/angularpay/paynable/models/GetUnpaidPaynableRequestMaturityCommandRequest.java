package io.angularpay.paynable.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class GetUnpaidPaynableRequestMaturityCommandRequest extends AccessControl {

    GetUnpaidPaynableRequestMaturityCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}

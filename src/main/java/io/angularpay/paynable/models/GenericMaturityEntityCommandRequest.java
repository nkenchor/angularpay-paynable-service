package io.angularpay.paynable.models;

import io.angularpay.paynable.validation.ValidUTCDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GenericMaturityEntityCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotEmpty
    @ValidUTCDate
    private String maturesOn;

    GenericMaturityEntityCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}

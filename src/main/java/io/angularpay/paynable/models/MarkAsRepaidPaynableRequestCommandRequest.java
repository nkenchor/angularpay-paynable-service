package io.angularpay.paynable.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MarkAsRepaidPaynableRequestCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    MarkAsRepaidPaynableRequestCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}

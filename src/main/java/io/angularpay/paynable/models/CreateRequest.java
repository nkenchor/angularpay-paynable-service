
package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.paynable.domain.Amount;
import io.angularpay.paynable.validation.ValidUTCDate;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRequest {

    @NotNull
    @Valid
    private Amount amount;

    @NotNull
    @JsonProperty("interest_rate_percent")
    private int interestRatePercent;

    @NotEmpty
    @ValidUTCDate
    @JsonProperty("matures_on")
    private String maturesOn;
}

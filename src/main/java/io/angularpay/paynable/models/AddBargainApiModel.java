
package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddBargainApiModel {

    @JsonProperty("interest_rate_percent")
    private int interestRatePercent;

    @NotEmpty
    private String comment;
}

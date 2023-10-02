package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InterestRatePercentModel {

    @JsonProperty("interest_rate_percent")
    private int interestRatePercent;
}

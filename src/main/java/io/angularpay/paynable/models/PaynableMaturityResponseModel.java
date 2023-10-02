package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.paynable.domain.RepaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaynableMaturityResponseModel {
    private String reference;
    @JsonProperty("matures_on")
    private String maturesOn;
    private RepaymentStatus status;
}

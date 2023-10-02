package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestmentPartyMaturityMessageResponseModel {
    @JsonProperty("user_reference")
    private String userReference;
    private MaturityMessage message;
}

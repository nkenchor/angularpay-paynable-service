package io.angularpay.paynable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.paynable.validation.ValidUTCDate;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MaturityDateModel {

    @NotEmpty
    @ValidUTCDate
    @JsonProperty("matures_on")
    private String maturesOn;
}

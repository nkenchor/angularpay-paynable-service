package io.angularpay.paynable.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaturityMessage {
    private String sms;
    private String email;
}

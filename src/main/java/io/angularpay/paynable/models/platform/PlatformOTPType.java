
package io.angularpay.paynable.models.platform;

import lombok.Data;

@Data
public class PlatformOTPType {

    private String code;
    private Boolean enabled;
    private String reference;

}

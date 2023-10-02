
package io.angularpay.paynable.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("paynable_requests_maturity")
public class PaynableRequestMaturity {

    @Id
    private String id;
    @Version
    private int version;
    private String reference;
    @JsonProperty("matures_on")
    private String maturesOn;
    private RepaymentStatus status;
}

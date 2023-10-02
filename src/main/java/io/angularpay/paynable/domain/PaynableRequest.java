
package io.angularpay.paynable.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("paynable_requests")
public class PaynableRequest {

    @Id
    private String id;
    @Version
    private int version;
    @JsonProperty("service_code")
    private String serviceCode;
    private boolean verified;
    @JsonProperty("verified_on")
    private String verifiedOn;
    private Amount amount;
    private Bargain bargain;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("interest_rate_percent")
    private int interestRatePercent;
    @JsonProperty("matures_on")
    private String maturesOn;
    private Investee investee;
    private List<Investor> investors;
    @JsonProperty("last_modified")
    private String lastModified;
    private String reference;
    @JsonProperty("request_tag")
    private String requestTag;
    private RequestStatus status;
}

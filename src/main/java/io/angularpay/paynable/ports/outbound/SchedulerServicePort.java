package io.angularpay.paynable.ports.outbound;

import io.angularpay.paynable.models.SchedulerServiceRequest;
import io.angularpay.paynable.models.SchedulerServiceResponse;

import java.util.Map;
import java.util.Optional;

public interface SchedulerServicePort {
    Optional<SchedulerServiceResponse> createScheduledRequest(SchedulerServiceRequest request, Map<String, String> headers);
}

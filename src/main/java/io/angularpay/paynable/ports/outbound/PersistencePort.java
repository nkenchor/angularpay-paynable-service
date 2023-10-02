package io.angularpay.paynable.ports.outbound;

import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.RepaymentStatus;
import io.angularpay.paynable.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    PaynableRequest createRequest(PaynableRequest request);
    PaynableRequest updateRequest(PaynableRequest request);
    Optional<PaynableRequest> findRequestByReference(String reference);
    Page<PaynableRequest> listRequests(Pageable pageable);
    PaynableRequestMaturity saveMaturity(PaynableRequestMaturity paynableRequestMaturity);
    Optional<PaynableRequestMaturity> findRequestMaturityByReference(String reference);
    Collection<PaynableRequestMaturity> findMaturityByStatus(RepaymentStatus status);
    Collection<PaynableRequestMaturity> listMaturity();
    Page<PaynableRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses);
    Page<PaynableRequest> findRequestsByVerification(Pageable pageable, boolean verified);
    Page<PaynableRequest> findByInvesteeUserReference(Pageable pageable, String userReference);
    long getCountByVerificationStatus(boolean verified);
    long getCountByRequestStatus(RequestStatus status);
    long getTotalCount();
}

package io.angularpay.paynable.adapters.outbound;

import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaynableRepository extends MongoRepository<PaynableRequest, String> {

    Optional<PaynableRequest> findByReference(String reference);
    Page<PaynableRequest> findAll(Pageable pageable);
    Page<PaynableRequest> findByStatusIn(Pageable pageable, List<RequestStatus> statuses);
    Page<PaynableRequest> findByVerified(Pageable pageable, boolean verified);
    Page<PaynableRequest> findAByInvesteeUserReference(Pageable pageable, String userReference);
    long countByVerified(boolean verified);
    long countByStatus(RequestStatus status);
}

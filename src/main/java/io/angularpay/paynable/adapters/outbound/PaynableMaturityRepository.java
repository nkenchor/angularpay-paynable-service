package io.angularpay.paynable.adapters.outbound;

import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.RepaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaynableMaturityRepository extends MongoRepository<PaynableRequestMaturity, String> {

    Optional<PaynableRequestMaturity> findByReference(String reference);
    List<PaynableRequestMaturity> findByStatus(RepaymentStatus status);
    Page<PaynableRequestMaturity> findAll(Pageable pageable);
}

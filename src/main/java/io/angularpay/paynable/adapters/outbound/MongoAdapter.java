package io.angularpay.paynable.adapters.outbound;

import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.RepaymentStatus;
import io.angularpay.paynable.domain.RequestStatus;
import io.angularpay.paynable.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final PaynableRepository paynableRepository;
    private final PaynableMaturityRepository paynableMaturityRepository;

    @Override
    public PaynableRequest createRequest(PaynableRequest request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return paynableRepository.save(request);
    }

    @Override
    public PaynableRequest updateRequest(PaynableRequest request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return paynableRepository.save(request);
    }

    @Override
    public Optional<PaynableRequest> findRequestByReference(String reference) {
        return paynableRepository.findByReference(reference);
    }

    @Override
    public Page<PaynableRequest> listRequests(Pageable pageable) {
        return paynableRepository.findAll(pageable);
    }

    @Override
    public PaynableRequestMaturity saveMaturity(PaynableRequestMaturity request) {
        return paynableMaturityRepository.save(request);
    }

    @Override
    public Optional<PaynableRequestMaturity> findRequestMaturityByReference(String reference) {
        return paynableMaturityRepository.findByReference(reference);
    }

    @Override
    public List<PaynableRequestMaturity> findMaturityByStatus(RepaymentStatus status) {
        return paynableMaturityRepository.findByStatus(status);
    }

    @Override
    public List<PaynableRequestMaturity> listMaturity() {
        return paynableMaturityRepository.findAll();
    }

    @Override
    public Page<PaynableRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses) {
        return paynableRepository.findByStatusIn(pageable, statuses);
    }

    @Override
    public Page<PaynableRequest> findRequestsByVerification(Pageable pageable, boolean verified) {
        return paynableRepository.findByVerified(pageable, verified);
    }

    @Override
    public Page<PaynableRequest> findByInvesteeUserReference(Pageable pageable, String userReference) {
        return paynableRepository.findAByInvesteeUserReference(pageable, userReference);
    }

    @Override
    public long getCountByVerificationStatus(boolean verified) {
        return paynableRepository.countByVerified(verified);
    }

    @Override
    public long getCountByRequestStatus(RequestStatus status) {
        return paynableRepository.countByStatus(status);
    }

    @Override
    public long getTotalCount() {
        return paynableRepository.count();
    }
}

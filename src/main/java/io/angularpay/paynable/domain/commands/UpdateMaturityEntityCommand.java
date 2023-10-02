package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.models.GenericCommandResponse;
import io.angularpay.paynable.models.GenericMaturityEntityCommandRequest;
import io.angularpay.paynable.models.GenericReferenceResponse;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.angularpay.paynable.domain.RepaymentStatus.UNPAID;


@Service
public class UpdateMaturityEntityCommand extends AbstractCommand<GenericMaturityEntityCommandRequest, GenericReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public UpdateMaturityEntityCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("UpdateMaturityEntityCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GenericMaturityEntityCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference(); // TODO use service account
    }

    @Override
    protected GenericCommandResponse handle(GenericMaturityEntityCommandRequest request) {
        Optional<PaynableRequestMaturity> optionalPeerFundRequestMaturity = this.mongoAdapter.findRequestMaturityByReference(request.getRequestReference());

        PaynableRequestMaturity response;
        if (optionalPeerFundRequestMaturity.isPresent()) {
            PaynableRequestMaturity foundMaturity = optionalPeerFundRequestMaturity.get();
            foundMaturity.setMaturesOn(request.getMaturesOn());
            response = this.mongoAdapter.saveMaturity(foundMaturity);
        } else {
            PaynableRequestMaturity paynableRequestMaturity = PaynableRequestMaturity.builder()
                    .reference(request.getRequestReference())
                    .maturesOn(request.getMaturesOn())
                    .status(UNPAID)
                    .build();
            response = this.mongoAdapter.saveMaturity(paynableRequestMaturity);
        }

        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(GenericMaturityEntityCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}

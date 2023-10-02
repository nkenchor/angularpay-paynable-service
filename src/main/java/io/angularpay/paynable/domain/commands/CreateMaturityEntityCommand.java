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

import static io.angularpay.paynable.domain.RepaymentStatus.UNPAID;

@Service
public class CreateMaturityEntityCommand extends AbstractCommand<GenericMaturityEntityCommandRequest, GenericReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public CreateMaturityEntityCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("CreateMaturityEntityCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GenericMaturityEntityCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference(); // TODO use service account
    }

    @Override
    protected GenericCommandResponse handle(GenericMaturityEntityCommandRequest request) {
        PaynableRequestMaturity paynableRequestMaturity = PaynableRequestMaturity.builder()
                .reference(request.getRequestReference())
                .maturesOn(request.getMaturesOn())
                .status(UNPAID)
                .build();
        PaynableRequestMaturity response = this.mongoAdapter.saveMaturity(paynableRequestMaturity);
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

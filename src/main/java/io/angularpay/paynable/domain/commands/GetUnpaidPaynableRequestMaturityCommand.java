package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.models.GetUnpaidPaynableRequestMaturityCommandRequest;
import io.angularpay.paynable.models.PaynableMaturityResponseModel;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.angularpay.paynable.domain.RepaymentStatus.UNPAID;

@Service
public class GetUnpaidPaynableRequestMaturityCommand extends AbstractCommand<GetUnpaidPaynableRequestMaturityCommandRequest, List<PaynableMaturityResponseModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUnpaidPaynableRequestMaturityCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("GetUnpaidPaynableRequestMaturityCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetUnpaidPaynableRequestMaturityCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<PaynableMaturityResponseModel> handle(GetUnpaidPaynableRequestMaturityCommandRequest request) {
        List<PaynableRequestMaturity> paynableRequestMaturityList = this.mongoAdapter.findMaturityByStatus(UNPAID);
        if (CollectionUtils.isEmpty(paynableRequestMaturityList)) return Collections.emptyList();
        return paynableRequestMaturityList.stream()
                .map(x-> PaynableMaturityResponseModel.builder()
                        .reference(x.getReference())
                        .maturesOn(x.getMaturesOn())
                        .status(x.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    protected List<ErrorObject> validate(GetUnpaidPaynableRequestMaturityCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}

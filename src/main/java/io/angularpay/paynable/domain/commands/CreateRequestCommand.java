package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.Investee;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.helpers.CommandHelper;
import io.angularpay.paynable.models.*;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.angularpay.paynable.helpers.ObjectFactory.paynableRequestWithDefaults;

@Slf4j
@Service
public class CreateRequestCommand extends AbstractCommand<CreateRequestCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;
    private final CreateMaturityEntityCommand createMaturityEntityCommand;

    public CreateRequestCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper,
            RedisAdapter redisAdapter,
            CreateMaturityEntityCommand createMaturityEntityCommand) {
        super("CreateRequestCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
        this.createMaturityEntityCommand = createMaturityEntityCommand;
    }

    @Override
    protected String getResourceOwner(CreateRequestCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected GenericCommandResponse handle(CreateRequestCommandRequest request) {
        PaynableRequest paynableRequestWithDefaults = paynableRequestWithDefaults();
        PaynableRequest withOtherDetails = paynableRequestWithDefaults.toBuilder()
                .amount(request.getCreateRequest().getAmount())
                .interestRatePercent(request.getCreateRequest().getInterestRatePercent())
                .maturesOn(request.getCreateRequest().getMaturesOn())
                .investee(Investee.builder()
                        .userReference(request.getAuthenticatedUser().getUserReference())
                        .build())
                .build();
        PaynableRequest response = this.mongoAdapter.createRequest(withOtherDetails);

        GenericMaturityEntityCommandRequest genericMaturityEntityCommandRequest = GenericMaturityEntityCommandRequest.builder()
                .requestReference(response.getReference())
                .maturesOn(request.getCreateRequest().getMaturesOn())
                .authenticatedUser(request.getAuthenticatedUser())
                .build();
        this.createMaturityEntityCommand.execute(genericMaturityEntityCommandRequest);

        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(CreateRequestCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public String convertToUpdatesMessage(PaynableRequest paynableRequest) throws JsonProcessingException {
        return this.commandHelper.toJsonString(paynableRequest);
    }

    @Override
    public RedisAdapter getRedisAdapter() {
        return this.redisAdapter;
    }

    @Override
    public ResourceReferenceResponse map(GenericCommandResponse genericCommandResponse) {
        return new ResourceReferenceResponse(genericCommandResponse.getRequestReference());
    }
}

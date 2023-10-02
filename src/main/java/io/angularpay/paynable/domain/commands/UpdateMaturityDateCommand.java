package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.helpers.CommandHelper;
import io.angularpay.paynable.models.GenericCommandResponse;
import io.angularpay.paynable.models.GenericMaturityEntityCommandRequest;
import io.angularpay.paynable.models.GenericReferenceResponse;
import io.angularpay.paynable.models.UpdateMaturityDateCommandRequest;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.helpers.CommandHelper.validRequestStatusOrThrow;

@Service
public class UpdateMaturityDateCommand extends AbstractCommand<UpdateMaturityDateCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;
    private final UpdateMaturityEntityCommand updateMaturityEntityCommand;

    public UpdateMaturityDateCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper,
            RedisAdapter redisAdapter,
            UpdateMaturityEntityCommand updateMaturityEntityCommand) {
        super("UpdateMaturityDateCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
        this.updateMaturityEntityCommand = updateMaturityEntityCommand;
    }

    @Override
    protected String getResourceOwner(UpdateMaturityDateCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateMaturityDateCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateMaturesOn(request);
        GenericCommandResponse response = this.commandHelper.executeAcid(supplier);

        GenericMaturityEntityCommandRequest genericMaturityEntityCommandRequest = GenericMaturityEntityCommandRequest.builder()
                .requestReference(found.getReference())
                .maturesOn(request.getMaturityDateModel().getMaturesOn())
                .authenticatedUser(request.getAuthenticatedUser())
                .build();
        this.updateMaturityEntityCommand.execute(genericMaturityEntityCommandRequest);

        return response;
    }

    private GenericCommandResponse updateMaturesOn(UpdateMaturityDateCommandRequest request) throws OptimisticLockingFailureException {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        PaynableRequest response = this.commandHelper.updateProperty(found, request.getMaturityDateModel()::getMaturesOn, found::setMaturesOn);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateMaturityDateCommandRequest request) {
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
}

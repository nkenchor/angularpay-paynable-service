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
import io.angularpay.paynable.models.GenericReferenceResponse;
import io.angularpay.paynable.models.UpdateAmountCommandRequest;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.helpers.CommandHelper.validRequestStatusOrThrow;

@Slf4j
@Service
public class UpdateAmountCommand extends AbstractCommand<UpdateAmountCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public UpdateAmountCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("UpdateAmountCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(UpdateAmountCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateAmountCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateAmount(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateAmount(UpdateAmountCommandRequest request) throws OptimisticLockingFailureException {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        PaynableRequest response = this.commandHelper.updateProperty(found, request::getAmount, found::setAmount);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateAmountCommandRequest request) {
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

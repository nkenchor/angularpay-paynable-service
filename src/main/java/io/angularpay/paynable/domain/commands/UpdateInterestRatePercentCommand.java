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
import io.angularpay.paynable.models.UpdateInterestRatePercentCommandRequest;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.helpers.CommandHelper.validRequestStatusOrThrow;

@Service
public class UpdateInterestRatePercentCommand extends AbstractCommand<UpdateInterestRatePercentCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public UpdateInterestRatePercentCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("UpdateInterestRatePercentCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(UpdateInterestRatePercentCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateInterestRatePercentCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateExchangeRate(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateExchangeRate(UpdateInterestRatePercentCommandRequest request) throws OptimisticLockingFailureException {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        PaynableRequest response = this.commandHelper.updateProperty(found, request::getInterestRatePercent, found::setInterestRatePercent);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateInterestRatePercentCommandRequest request) {
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

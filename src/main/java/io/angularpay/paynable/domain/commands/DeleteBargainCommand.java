package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.helpers.CommandHelper;
import io.angularpay.paynable.models.DeleteBargainCommandRequest;
import io.angularpay.paynable.models.GenericCommandResponse;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.helpers.CommandHelper.validRequestStatusAndBargainExists;


@Service
public class DeleteBargainCommand extends AbstractCommand<DeleteBargainCommandRequest, GenericCommandResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public DeleteBargainCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator, CommandHelper commandHelper, RedisAdapter redisAdapter) {
        super("DeleteBargainCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(DeleteBargainCommandRequest request) {
        return this.commandHelper.getBargainOwner(request.getRequestReference(), request.getBargainReference());
    }

    @Override
    protected GenericCommandResponse handle(DeleteBargainCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusAndBargainExists(found, request.getBargainReference());
        Supplier<GenericCommandResponse> supplier = () -> deleteBargain(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse deleteBargain(DeleteBargainCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        found.getBargain().getOffers().forEach(x-> {
            if (request.getBargainReference().equalsIgnoreCase(x.getReference())) {
                x.setDeleted(true);
                x.setDeletedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
            }
        });
        if (request.getBargainReference().equalsIgnoreCase(found.getBargain().getAcceptedBargainReference())) {
            found.getBargain().setAcceptedBargainReference(null);
        }
        PaynableRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(DeleteBargainCommandRequest request) {
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

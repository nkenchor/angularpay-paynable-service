package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.helpers.CommandHelper;
import io.angularpay.paynable.models.*;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.helpers.CommandHelper.validRequestStatusOrThrow;
import static io.angularpay.paynable.helpers.Helper.getAllParties;
import static io.angularpay.paynable.models.UserNotificationType.INVESTMENT_VERIFIED;

@Service
public class UpdateVerificationStatusCommand extends AbstractCommand<UpdateVerificationStatusCommandRequest, GenericReferenceResponse>
        implements UpdatesPublisherCommand<GenericCommandResponse>,
        UserNotificationsPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;
    private final RedisAdapter redisAdapter;

    public UpdateVerificationStatusCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper,
            RedisAdapter redisAdapter) {
        super("UpdateVerificationStatusCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(UpdateVerificationStatusCommandRequest request) {
        return "";
    }

    @Override
    protected GenericCommandResponse handle(UpdateVerificationStatusCommandRequest request) {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateVerificationStatus(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateVerificationStatus(UpdateVerificationStatusCommandRequest request) throws OptimisticLockingFailureException {
        PaynableRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        found.setVerified(request.getVerified());
        found.setVerifiedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        PaynableRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .paynableRequest(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateVerificationStatusCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_KYC_ADMIN, Role.ROLE_PLATFORM_ADMIN);
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
    public UserNotificationType getUserNotificationType(GenericCommandResponse commandResponse) {
        return INVESTMENT_VERIFIED;
    }

    @Override
    public List<String> getAudience(GenericCommandResponse commandResponse) {
        return getAllParties(commandResponse.getPaynableRequest());
    }

    @Override
    public String convertToUserNotificationsMessage(UserNotificationBuilderParameters<GenericCommandResponse, PaynableRequest> parameters) throws JsonProcessingException {
        String status = parameters.getRequest().isVerified()? "verified": "unverified";
        String summary;
        if (parameters.getUserReference().equalsIgnoreCase(parameters.getRequest().getInvestee().getUserReference())) {
            summary = "your Salary Advance post was marked as: " + status;
        } else {
            summary = "a Salary Advance post you commented on was marked as: " + status;
        }

        UserNotificationRequestPayload userNotificationInvestmentPayload = UserNotificationRequestPayload.builder()
                .requestReference(parameters.getCommandResponse().getRequestReference())
                .build();
        String payload = mapper.writeValueAsString(userNotificationInvestmentPayload);

        String attributes = mapper.writeValueAsString(parameters.getRequest());

        UserNotification userNotification = UserNotification.builder()
                .reference(UUID.randomUUID().toString())
                .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .serviceCode(parameters.getRequest().getServiceCode())
                .userReference(parameters.getUserReference())
                .type(parameters.getType())
                .summary(summary)
                .payload(payload)
                .attributes(attributes)
                .build();

        return mapper.writeValueAsString(userNotification);
    }
}

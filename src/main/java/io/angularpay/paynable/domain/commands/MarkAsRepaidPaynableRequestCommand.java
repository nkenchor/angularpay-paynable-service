package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.adapters.outbound.RedisAdapter;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.PaynableRequestMaturity;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.CommandException;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.models.*;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.angularpay.paynable.domain.RepaymentStatus.REPAID;
import static io.angularpay.paynable.exceptions.ErrorCode.REQUEST_NOT_FOUND;
import static io.angularpay.paynable.helpers.Helper.getAllParties;
import static io.angularpay.paynable.models.UserNotificationType.INVESTMENT_REPAID;

@Service
public class MarkAsRepaidPaynableRequestCommand extends AbstractCommand<MarkAsRepaidPaynableRequestCommandRequest, GenericReferenceResponse>
    implements UserNotificationsPublisherCommand<GenericCommandResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final RedisAdapter redisAdapter;

    public MarkAsRepaidPaynableRequestCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            RedisAdapter redisAdapter) {
        super("MarkAsRepaidPaynableRequestCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.redisAdapter = redisAdapter;
    }

    @Override
    protected String getResourceOwner(MarkAsRepaidPaynableRequestCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference(); // TODO use service account
    }

    @Override
    protected GenericCommandResponse handle(MarkAsRepaidPaynableRequestCommandRequest request) {
        PaynableRequestMaturity found = this.mongoAdapter.findRequestMaturityByReference(request.getRequestReference())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build()
                );
        found.setStatus(REPAID);
        PaynableRequestMaturity response = this.mongoAdapter.saveMaturity(found);

        return GenericCommandResponse.builder()
                .requestReference(response.getReference())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(MarkAsRepaidPaynableRequestCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public RedisAdapter getRedisAdapter() {
        return this.redisAdapter;
    }

    @Override
    public UserNotificationType getUserNotificationType(GenericCommandResponse commandResponse) {
        return INVESTMENT_REPAID;
    }

    @Override
    public List<String> getAudience(GenericCommandResponse commandResponse) {
        return getAllParties(commandResponse.getPaynableRequest());
    }

    @Override
    public String convertToUserNotificationsMessage(UserNotificationBuilderParameters<GenericCommandResponse, PaynableRequest> parameters) throws JsonProcessingException {
        String summary;
        if (parameters.getUserReference().equalsIgnoreCase(parameters.getRequest().getInvestee().getUserReference())) {
            summary = "your Salary Advance post has been repaid on maturity";
        } else {
            summary = "a Salary Advance post you commented on has been repaid on maturity";
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

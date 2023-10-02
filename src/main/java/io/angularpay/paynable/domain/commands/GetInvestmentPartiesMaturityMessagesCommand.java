package io.angularpay.paynable.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.Role;
import io.angularpay.paynable.exceptions.ErrorObject;
import io.angularpay.paynable.models.GetRequestByReferenceCommandRequest;
import io.angularpay.paynable.models.InvestmentPartyMaturityMessageResponseModel;
import io.angularpay.paynable.models.MaturityMessage;
import io.angularpay.paynable.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.angularpay.paynable.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.paynable.util.InvestmentPartyMaturityMessageGenerator.*;

@Service
public class GetInvestmentPartiesMaturityMessagesCommand extends AbstractCommand<GetRequestByReferenceCommandRequest, List<InvestmentPartyMaturityMessageResponseModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetInvestmentPartiesMaturityMessagesCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetInvestmentPartiesMaturityMessagesCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetRequestByReferenceCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<InvestmentPartyMaturityMessageResponseModel> handle(GetRequestByReferenceCommandRequest request) {
        PaynableRequest response = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());

        List<InvestmentPartyMaturityMessageResponseModel> messageList = new ArrayList<>();

        InvestmentPartyMaturityMessageResponseModel investeeMessage = InvestmentPartyMaturityMessageResponseModel.builder()
                .userReference(response.getInvestee().getUserReference())
                .message(MaturityMessage.builder()
                        .sms(formatInvesteeSms(response))
                        .email(formatInvesteeEmail(response))
                        .build())
                .build();
        messageList.add(investeeMessage);

        response.getInvestors().forEach(investor -> {
            InvestmentPartyMaturityMessageResponseModel investorMessage = InvestmentPartyMaturityMessageResponseModel.builder()
                    .userReference(investor.getUserReference())
                    .message(MaturityMessage.builder()
                            .sms(formatInvestorSms(response, investor))
                            .email(formatInvestorEmail(response, investor))
                            .build())
                    .build();
            messageList.add(investorMessage);
        });

        return messageList;
    }

    @Override
    protected List<ErrorObject> validate(GetRequestByReferenceCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}

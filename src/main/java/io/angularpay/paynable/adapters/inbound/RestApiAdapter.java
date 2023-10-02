package io.angularpay.paynable.adapters.inbound;

import io.angularpay.paynable.configurations.AngularPayConfiguration;
import io.angularpay.paynable.domain.*;
import io.angularpay.paynable.domain.commands.*;
import io.angularpay.paynable.models.*;
import io.angularpay.paynable.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.angularpay.paynable.domain.DeletedBy.*;
import static io.angularpay.paynable.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/paynable/requests")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final AngularPayConfiguration configuration;

    private final CreateRequestCommand createRequestCommand;
    private final UpdateAmountCommand updateAmountCommand;
    private final UpdateInterestRatePercentCommand updateInterestRatePercentCommand;
    private final UpdateMaturityDateCommand updateMaturityDateCommand;
    private final UpdateVerificationStatusCommand updateVerificationStatusCommand;
    private final AddInvestorCommand addInvestorCommand;
    private final RemoveInvestorCommand removeInvestorCommand;
    private final AddBargainCommand addBargainCommand;
    private final AcceptBargainCommand acceptBargainCommand;
    private final RejectBargainCommand rejectBargainCommand;
    private final DeleteBargainCommand deleteBargainCommand;
    private final UpdateInvestmentAmountCommand updateInvestmentAmountCommand;
    private final MakePaymentCommand makePaymentCommand;
    private final UpdateRequestStatusCommand updateRequestStatusCommand;
    private final GetRequestByReferenceCommand getRequestByReferenceCommand;
    private final GetNewsfeedCommand getNewsfeedCommand;
    private final GetUserRequestsCommand getUserRequestsCommand;
    private final GetUserInvestmentsCommand getUserInvestmentsCommand;
    private final MarkAsRepaidPaynableRequestCommand markAsRepaidPaynableRequestCommand;
    private final GetUnpaidPaynableRequestMaturityCommand getUnpaidPaynableRequestMaturityCommand;
    private final GetInvestmentPartiesMaturityMessagesCommand getInvestmentPartiesMaturityMessagesCommand;
    private final GetNewsfeedByStatusCommand getNewsfeedByStatusCommand;
    private final GetRequestListByStatusCommand getRequestListByStatusCommand;
    private final GetRequestListByVerificationCommand getRequestListByVerificationCommand;
    private final GetRequestListCommand getRequestListCommand;
    private final ScheduledRequestCommand scheduledRequestCommand;
    private final GetStatisticsCommand getStatisticsCommand;

    @PostMapping("/schedule/{schedule}")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse createScheduledRequest(
            @PathVariable String schedule,
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        ScheduledRequestCommandRequest scheduledRequestCommandRequest = ScheduledRequestCommandRequest.builder()
                .runAt(schedule)
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return scheduledRequestCommand.execute(scheduledRequestCommandRequest);
    }
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse create(
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        CreateRequestCommandRequest createRequestCommandRequest = CreateRequestCommandRequest.builder()
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return createRequestCommand.execute(createRequestCommandRequest);
    }

    @PutMapping("/{requestReference}/amount")
    @Override
    public void updateAmount(
            @PathVariable String requestReference,
            @RequestBody Amount amount,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateAmountCommandRequest updateAmountCommandRequest = UpdateAmountCommandRequest.builder()
                .requestReference(requestReference)
                .amount(amount)
                .authenticatedUser(authenticatedUser)
                .build();
        updateAmountCommand.execute(updateAmountCommandRequest);
    }

    @PutMapping("{requestReference}/interest-rate")
    @Override
    public void updateInterestRatePercent(
            @PathVariable String requestReference,
            @RequestBody InterestRatePercentModel interestRatePercent,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateInterestRatePercentCommandRequest updateInterestRatePercentCommandRequest = UpdateInterestRatePercentCommandRequest.builder()
                .requestReference(requestReference)
                .interestRatePercent(interestRatePercent.getInterestRatePercent())
                .authenticatedUser(authenticatedUser)
                .build();
        updateInterestRatePercentCommand.execute(updateInterestRatePercentCommandRequest);
    }

    @PutMapping("{requestReference}/maturity/date")
    @Override
    public void updateMaturityDate(
            @PathVariable String requestReference,
            @RequestBody MaturityDateModel maturityDateModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateMaturityDateCommandRequest updateMaturityDateCommandRequest = UpdateMaturityDateCommandRequest.builder()
                .requestReference(requestReference)
                .maturityDateModel(maturityDateModel)
                .authenticatedUser(authenticatedUser)
                .build();
        updateMaturityDateCommand.execute(updateMaturityDateCommandRequest);
    }

    @PutMapping("/{requestReference}/verify/{verified}")
    @Override
    public void updateVerificationStatus(
            @PathVariable String requestReference,
            @PathVariable boolean verified,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateVerificationStatusCommandRequest updateVerificationStatusCommandRequest = UpdateVerificationStatusCommandRequest.builder()
                .requestReference(requestReference)
                .verified(verified)
                .authenticatedUser(authenticatedUser)
                .build();
        updateVerificationStatusCommand.execute(updateVerificationStatusCommandRequest);
    }

    @PostMapping("/{requestReference}/investors")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse addInvestor(
            @PathVariable String requestReference,
            @RequestBody AddInvestorApiModel addInvestorApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AddInvestorCommandRequest addInvestorCommandRequest = AddInvestorCommandRequest.builder()
                .requestReference(requestReference)
                .addInvestorApiModel(addInvestorApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return addInvestorCommand.execute(addInvestorCommandRequest);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}")
    @Override
    public void removeInvestor(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, INVESTOR);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}/ttl")
    @Override
    public void removeInvestorTTL(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, TTL_SERVICE);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}/platform")
    @Override
    public void removeInvestorPlatform(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, PLATFORM);
    }

    private void removeInvestor(
            String requestReference,
            String investmentReference,
            Map<String, String> headers,
            DeletedBy deletedBy) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RemoveInvestorCommandRequest removeInvestorCommandRequest = RemoveInvestorCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .deletedBy(deletedBy)
                .authenticatedUser(authenticatedUser)
                .build();
        removeInvestorCommand.execute(removeInvestorCommandRequest);
    }

    @PostMapping("{requestReference}/bargains")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse addBargain(
            @PathVariable String requestReference,
            @RequestBody AddBargainApiModel addBargainApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AddBargainCommandRequest addBargainCommandRequest = AddBargainCommandRequest.builder()
                .requestReference(requestReference)
                .addBargainApiModel(addBargainApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return addBargainCommand.execute(addBargainCommandRequest);
    }

    @PutMapping("{requestReference}/bargains/{bargainReference}")
    @Override
    public void acceptBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AcceptBargainCommandRequest acceptBargainCommandRequest = AcceptBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        acceptBargainCommand.execute(acceptBargainCommandRequest);
    }

    @DeleteMapping("{requestReference}/bargains/{bargainReference}")
    @Override
    public void rejectBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RejectBargainCommandRequest rejectBargainCommandRequest = RejectBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        rejectBargainCommand.execute(rejectBargainCommandRequest);
    }

    @DeleteMapping("{requestReference}/bargains/{bargainReference}/delete")
    @Override
    public void deleteBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        DeleteBargainCommandRequest deleteBargainCommandRequest = DeleteBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        deleteBargainCommand.execute(deleteBargainCommandRequest);
    }

    @PutMapping("/{requestReference}/investors/{investmentReference}/amount")
    @Override
    public void updateInvestmentAmount(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestBody UpdateInvestmentApiModel updateInvestmentApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateInvestmentAmountCommandRequest updateInvestmentAmountCommandRequest = UpdateInvestmentAmountCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .updateInvestmentApiModel(updateInvestmentApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        updateInvestmentAmountCommand.execute(updateInvestmentAmountCommandRequest);
    }

    @PostMapping("{requestReference}/investors/{investmentReference}/payment")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse makePayment(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestBody PaymentRequest paymentRequest,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        MakePaymentCommandRequest makePaymentCommandRequest = MakePaymentCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .paymentRequest(paymentRequest)
                .authenticatedUser(authenticatedUser)
                .build();
        return makePaymentCommand.execute(makePaymentCommandRequest);
    }

    @PutMapping("/{requestReference}/status")
    @Override
    public void updateRequestStatus(
            @PathVariable String requestReference,
            @RequestBody RequestStatusModel status,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateRequestStatusCommandRequest updateRequestStatusCommandRequest = UpdateRequestStatusCommandRequest.builder()
                .requestReference(requestReference)
                .status(status.getStatus())
                .authenticatedUser(authenticatedUser)
                .build();
        updateRequestStatusCommand.execute(updateRequestStatusCommandRequest);
    }

    @GetMapping("/{requestReference}")
    @ResponseBody
    @Override
    public PaynableRequest getRequestByReference(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestByReferenceCommandRequest getRequestByReferenceCommandRequest = GetRequestByReferenceCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return getRequestByReferenceCommand.execute(getRequestByReferenceCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}")
    @ResponseBody
    @Override
    public List<PaynableRequest> getNewsfeedModel(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getNewsfeedCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/list/user-request/page/{page}")
    @ResponseBody
    @Override
    public List<UserRequestModel> getUserRequests(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserRequestsCommandRequest getUserRequestsCommandRequest = GetUserRequestsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserRequestsCommand.execute(getUserRequestsCommandRequest);
    }

    @GetMapping("/list/user-investment/page/{page}")
    @ResponseBody
    @Override
    public List<UserInvestmentModel> getUserInvestments(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserInvestmentsCommandRequest getUserInvestmentsCommandRequest = GetUserInvestmentsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserInvestmentsCommand.execute(getUserInvestmentsCommandRequest);
    }

    @PutMapping("{requestReference}/maturity/status/repaid")
    @Override
    public void markAsRepaidPeerFundRequest(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        MarkAsRepaidPaynableRequestCommandRequest markAsRepaidPaynableRequestCommandRequest = MarkAsRepaidPaynableRequestCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        this.markAsRepaidPaynableRequestCommand.execute(markAsRepaidPaynableRequestCommandRequest);
    }

    @GetMapping("/list/maturity/status/unpaid")
    @ResponseBody
    @Override
    public List<PaynableMaturityResponseModel> getUnpaidPaynableRequestMaturity(@RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUnpaidPaynableRequestMaturityCommandRequest getUnpaidPaynableRequestMaturityCommandRequest = GetUnpaidPaynableRequestMaturityCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return getUnpaidPaynableRequestMaturityCommand.execute(getUnpaidPaynableRequestMaturityCommandRequest);
    }

    @GetMapping("/{requestReference}/maturity/messages")
    @ResponseBody
    @Override
    public List<InvestmentPartyMaturityMessageResponseModel> getInvestmentPartiesMaturityMessages(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestByReferenceCommandRequest getRequestByReferenceCommandRequest = GetRequestByReferenceCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return getInvestmentPartiesMaturityMessagesCommand.execute(getRequestByReferenceCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<PaynableRequest> getNewsfeedByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getNewsfeedByStatusCommand.execute(genericGetByStatusCommandRequest);
    }

    @GetMapping("/list/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<PaynableRequest> getRequestListByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getRequestListByStatusCommand.execute(genericGetByStatusCommandRequest);
    }

    @GetMapping("/list/page/{page}/filter/verified/{verified}")
    @ResponseBody
    @Override
    public List<PaynableRequest> getRequestListByVerification(
            @PathVariable int page,
            @PathVariable boolean verified,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestListByVerificationCommandRequest getRequestListByVerificationCommandRequest = GetRequestListByVerificationCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .verified(verified)
                .build();
        return getRequestListByVerificationCommand.execute(getRequestListByVerificationCommandRequest);
    }

    @GetMapping("/list/page/{page}")
    @ResponseBody
    @Override
    public List<PaynableRequest> getRequestList(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getRequestListCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/statistics")
    @ResponseBody
    @Override
    public List<Statistics> getStatistics(@RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetStatisticsCommandRequest getStatisticsCommandRequest = GetStatisticsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return getStatisticsCommand.execute(getStatisticsCommandRequest);
    }
}

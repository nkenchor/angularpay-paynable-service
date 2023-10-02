package io.angularpay.paynable.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.paynable.adapters.outbound.MongoAdapter;
import io.angularpay.paynable.configurations.AngularPayConfiguration;
import io.angularpay.paynable.domain.Investor;
import io.angularpay.paynable.domain.Offer;
import io.angularpay.paynable.domain.PaynableRequest;
import io.angularpay.paynable.domain.RequestStatus;
import io.angularpay.paynable.exceptions.CommandException;
import io.angularpay.paynable.exceptions.ErrorCode;
import io.angularpay.paynable.models.GenericCommandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.angularpay.paynable.domain.InvestmentTransactionStatus.SUCCESSFUL;
import static io.angularpay.paynable.exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper mapper;
    private final AngularPayConfiguration configuration;

    public GenericCommandResponse executeAcid(Supplier<GenericCommandResponse> supplier) {
        int maxRetry = this.configuration.getMaxUpdateRetry();
        OptimisticLockingFailureException optimisticLockingFailureException;
        int counter = 0;
        //noinspection ConstantConditions
        do {
            try {
                return supplier.get();
            } catch (OptimisticLockingFailureException exception) {
                if (counter++ >= maxRetry) throw exception;
                optimisticLockingFailureException = exception;
            }
        }
        while (Objects.nonNull(optimisticLockingFailureException));
        throw optimisticLockingFailureException;
    }

    public String getRequestOwner(String requestReference) {
        PaynableRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getInvestee().getUserReference();
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

    public String getInvestmentOwner(String requestReference, String investmentReference) {
        PaynableRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        if (CollectionUtils.isEmpty(found.getInvestors())) return "";
        return found.getInvestors().stream()
                .filter(x -> investmentReference.equalsIgnoreCase(x.getReference()))
                .map(Investor::getUserReference)
                .findFirst()
                .orElse("");
    }

    public String getBargainOwner(String requestReference, String bargainReference) {
        PaynableRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        if (Objects.isNull(found.getBargain()) || CollectionUtils.isEmpty(found.getBargain().getOffers())) return "";
        return found.getBargain().getOffers().stream()
                .filter(x -> bargainReference.equalsIgnoreCase(x.getReference()))
                .map(Offer::getUserReference)
                .findFirst()
                .orElse("");
    }

    public <T> PaynableRequest updateProperty(PaynableRequest paynableRequest, Supplier<T> getter, Consumer<T> setter) {
        setter.accept(getter.get());
        return this.mongoAdapter.updateRequest(paynableRequest);
    }

    public <T> PaynableRequest addItemToCollection(PaynableRequest paynableRequest, T newProperty, Supplier<List<T>> collectionGetter, Consumer<List<T>> collectionSetter) {
        if (CollectionUtils.isEmpty(collectionGetter.get())) {
            collectionSetter.accept(new ArrayList<>());
        }
        collectionGetter.get().add(newProperty);
        return this.mongoAdapter.updateRequest(paynableRequest);
    }

    public <T> String toJsonString(T t) throws JsonProcessingException {
        return this.mapper.writeValueAsString(t);
    }

    public static PaynableRequest getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String requestReference) {
        return mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    public static void validRequestStatusAndBargainExists(PaynableRequest found, String bargainReference) {
        validRequestStatusOrThrow(found);
        if (Objects.isNull(found.getBargain()) || CollectionUtils.isEmpty(found.getBargain().getOffers())) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
        if (found.getBargain().getOffers().stream().noneMatch(x -> bargainReference.equalsIgnoreCase(x.getReference()))) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
    }

    public static void validRequestStatusAndInvestmentExists(PaynableRequest found, String investmentReference) {
        validRequestStatusOrThrow(found);
        if (CollectionUtils.isEmpty(found.getInvestors())) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
        if (found.getInvestors().stream().noneMatch(x -> investmentReference.equalsIgnoreCase(x.getReference()))) {
            throw commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND);
        }
    }

    public static void validRequestStatusOrThrow(PaynableRequest found) {
        if (found.getStatus() == RequestStatus.COMPLETED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
        if (found.getStatus() == RequestStatus.CANCELLED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_CANCELLED_ERROR);
        }
    }

    public static void validateInvestmentStatusOrThrow(Investor investor) {
        if (Objects.nonNull(investor.getInvestmentStatus()) && investor.getInvestmentStatus().getStatus() == SUCCESSFUL) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
    }
}

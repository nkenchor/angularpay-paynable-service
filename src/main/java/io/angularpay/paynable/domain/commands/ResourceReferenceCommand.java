package io.angularpay.paynable.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}

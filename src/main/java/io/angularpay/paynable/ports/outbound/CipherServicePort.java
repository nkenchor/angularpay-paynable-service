package io.angularpay.paynable.ports.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.paynable.models.VerifySignatureResponseModel;

import java.util.Map;

public interface CipherServicePort {
    VerifySignatureResponseModel verifySignature(String requestBody, Map<String, String> headers) throws JsonProcessingException;
}

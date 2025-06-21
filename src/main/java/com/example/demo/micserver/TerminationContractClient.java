package com.example.demo.micserver;

import com.example.demo.entity.Result;
import com.example.demo.micserver.request.TerminationContractRequest;
import com.example.demo.micserver.response.TerminationResponse;
import com.example.demo.modal.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class TerminationContractClient {

    private final RestTemplate restTemplate;

    public TerminationResponse callTerminationContract(String token,
                                                       TerminationContractRequest request,
                                                       Result result) {
        try {
            if (Objects.isNull(result)) {
                CompletableFuture.completedFuture(null);
            }
            String url = "https://bpm.mic.vn/api/payments/v1/termination-contract";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + token);
            headers.set("Accept-Language", "vi");
            headers.set("clientMessageId", UUID.randomUUID().toString());

            HttpEntity<TerminationContractRequest> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<TerminationResponse>> responseType =
                    new ParameterizedTypeReference<>() {
                    };

            ResponseEntity<ApiResponse<TerminationResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            if (Objects.isNull(response.getBody()) || response.getBody().getHttpStatus() != 200) {
                log.info("Termination contract failed, policy: {}", request.getPolicyNo());
                result.getListError().add(request.getPolicyNo());
                return null;
            } else {
                log.info("Termination contract done, policy: {}", request.getPolicyNo());
                result.getListDone().add(request.getPolicyNo());
                return response.getBody().getData();
            }

        } catch (Exception e) {
            return null;
        }
    }
}

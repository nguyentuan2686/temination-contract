package com.example.demo.micserver;

import com.example.demo.entity.Result;
import com.example.demo.micserver.request.InfoSearchRequest;
import com.example.demo.micserver.response.DetailContractResponse;
import com.example.demo.micserver.response.SearchResultData;
import com.example.demo.modal.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class DetailContractClient {

  private final RestTemplate restTemplate;

  public DetailContractResponse getDetailContract(
      String id, String policyNo, String token, Result result) {

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + token);
      headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.set("clientMessageId", UUID.randomUUID().toString());

      HttpEntity<Void> entity = new HttpEntity<>(headers);

      ParameterizedTypeReference<ApiResponse<DetailContractResponse>> responseType =
          new ParameterizedTypeReference<>() {
          };

      ResponseEntity<ApiResponse<DetailContractResponse>> response = restTemplate.exchange(
          "https://bpm.mic.vn/api/payments/v1/termination-contract/source/" + id,
          HttpMethod.GET,
          entity,
          responseType
      );

      if (Objects.isNull(response.getBody()) || response.getBody().getHttpStatus() != 200) {
        result.getListError().add(policyNo);
        return null;
      } else if (Objects.isNull(response.getBody().getData())) {
        result.getListNoContract().add(policyNo);
        return null;
      }
      return response.getBody().getData();
    } catch (Exception e) {
      return null;
    }
  }
}

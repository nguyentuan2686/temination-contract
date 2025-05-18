package com.example.demo.micserver;

import com.example.demo.entity.Result;
import com.example.demo.micserver.request.InfoSearchRequest;
import com.example.demo.micserver.response.ContractContent;
import com.example.demo.micserver.response.SearchResultData;
import com.example.demo.modal.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class GetInsInfoClient {
  private final RestTemplate restTemplate;

  public List<ContractContent> callSearchContract(
      InfoSearchRequest request, String token, Result result) {

    String url = "https://bpm.mic.vn/api/payments/v1/termination-contract/source/search?page=0&size=10";

    try {
      if(Objects.isNull(request)) {
        return null;
      }
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.set("Authorization", "Bearer " + token);
      headers.set("Accept-Language", "vi");
      headers.set("clientMessageId", UUID.randomUUID().toString());

      HttpEntity<InfoSearchRequest> entity = new HttpEntity<>(request, headers);

      ParameterizedTypeReference<ApiResponse<SearchResultData>> responseType =
          new ParameterizedTypeReference<>() {
          };

      ResponseEntity<ApiResponse<SearchResultData>> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          responseType
      );

      if (Objects.isNull(response.getBody()) || response.getBody().getHttpStatus() != 200) {
        result.getListError().add(request.getPolicyNo());
        return null;
      } else if (Objects.isNull(response.getBody().getData()) || response.getBody().getData().getTotalElements() < 1) {
        result.getListNoContract().add(request.getPolicyNo());
        return null;
      }
      return response.getBody().getData().getContent();
    } catch (Exception e) {
      return null;
    }
  }
}

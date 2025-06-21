package com.example.demo.micserver;

import com.example.demo.modal.ApiResponse;
import com.example.demo.micserver.response.TokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GetTokenClient {

  private final RestTemplate restTemplate;

  public TokenResponse getTokenByRefreshToken(String refreshToken) {
    String url = "https://bpm.mic.vn/key-cloak/api/keycloak/v1/users/token?refreshToken=" + refreshToken;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.set("clientMessageId", UUID.randomUUID().toString());

      HttpEntity<Void> entity = new HttpEntity<>(headers);

      ParameterizedTypeReference<ApiResponse<TokenResponse>> responseType =
          new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<TokenResponse>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          responseType
      );

      ApiResponse<TokenResponse> body = response.getBody();

      if (body == null || body.getHttpStatus() != 200 || body.getData() == null) {
        throw new RuntimeException("Failed to refresh token: " + (body != null ? body.getMessage() : "no response"));
      }

      return body.getData();
    } catch (Exception e) {
      throw new RuntimeException("Error while calling getTokenByRefreshToken", e);
    }
  }
}


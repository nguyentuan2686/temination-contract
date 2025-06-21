package com.example.demo.micserver;

import com.example.demo.micserver.request.LoginRequest;
import com.example.demo.micserver.response.TokenResponse;
import com.example.demo.modal.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LoginClient {

  private final RestTemplate restTemplate;

  public TokenResponse login(String username, String password) {
    String url = "https://bpm.mic.vn/key-cloak/api/keycloak/v1/users/token";
    try {
      LoginRequest request = new LoginRequest(username, password);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.set("clientMessageId", UUID.randomUUID().toString());

      HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

      ParameterizedTypeReference<ApiResponse<TokenResponse>> responseType =
          new ParameterizedTypeReference<>() {};

      ResponseEntity<ApiResponse<TokenResponse>> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          responseType
      );

      ApiResponse<TokenResponse> body = response.getBody();

      if (body == null || body.getHttpStatus() != 200 || body.getData() == null) {
        throw new RuntimeException("Login failed: " + (body != null ? body.getMessage() : "no response"));
      }

      return body.getData();

    } catch (Exception e) {
      throw new RuntimeException("Error during login", e);
    }
  }
}



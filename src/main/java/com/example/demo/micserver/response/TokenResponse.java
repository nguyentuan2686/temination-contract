package com.example.demo.micserver.response;
import lombok.Data;

@Data
public class TokenResponse {
  private String token;
  private String refreshToken;
}

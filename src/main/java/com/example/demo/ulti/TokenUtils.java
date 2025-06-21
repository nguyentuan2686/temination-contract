package com.example.demo.ulti;

import com.example.demo.filter.TokenHolder;
import com.example.demo.micserver.GetTokenClient;
import com.example.demo.micserver.response.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class TokenUtils {
  private final GetTokenClient getTokenClient;

  public void renewToken() {
    TokenResponse tokenByRefreshToken = getTokenClient.getTokenByRefreshToken(TokenHolder.getRefreshToken());
    TokenHolder.setRefreshToken(tokenByRefreshToken.getRefreshToken());
    TokenHolder.setToken(tokenByRefreshToken.getToken());
  }

  @Scheduled(fixedRate = 600_000) // 10 phút (ms)
  public void renewTokenScheduled() {
    try {
      log.info("renewTokenScheduled");
      String refreshToken = TokenHolder.getRefreshToken();
      if (refreshToken == null) {
        return;
      }
      TokenResponse tokenResponse = getTokenClient.getTokenByRefreshToken(refreshToken);
      if (tokenResponse != null && tokenResponse.getToken() != null) {
        TokenHolder.setToken(tokenResponse.getToken());
        TokenHolder.setRefreshToken(tokenResponse.getRefreshToken());
        log.info("renew token success");
      }
    } catch (Exception e) {
      log.error("renew token failed", e);
      e.printStackTrace();
    }
  }
}

package com.example.demo.micserver;

import com.example.demo.config.MultipartInputStreamFileResource;
import com.example.demo.entity.Result;
import com.example.demo.micserver.response.DetailContractResponse;
import com.example.demo.micserver.response.UploadFileResponse;
import com.example.demo.modal.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@AllArgsConstructor
public class AlfrescoUploadClient {
  private final RestTemplate restTemplate;

  public UploadFileResponse uploadFileToAlfresco(String token) {
    try {
      ClassPathResource pdfFile = new ClassPathResource("static/huy.pdf");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers.setAccept(List.of(MediaType.APPLICATION_JSON));
      headers.set("Authorization", "Bearer " + token);
      headers.set("Accept-Language", "vi");
      headers.set("clientMessageId", UUID.randomUUID().toString());

      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

      body.add("files", new MultipartInputStreamFileResource(pdfFile.getInputStream(), pdfFile.getFilename()));

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ParameterizedTypeReference<ApiResponse<List<UploadFileResponse>>> responseType =
          new ParameterizedTypeReference<>() {
          };

      String url = "https://bpm.mic.vn/ecm-alfresco/api/alfresco/v1/files/upload?autoRename=true";

      ResponseEntity<ApiResponse<List<UploadFileResponse>>> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          responseType
      );

      if (Objects.isNull(response.getBody()) || response.getBody().getHttpStatus() != 200 || CollectionUtils.isEmpty(response.getBody().getData())) {
        return null;
      }
      return response.getBody().getData().getFirst();

    } catch (Exception e) {
      log.error("Upload failed: ", e);
      return null;
    }
  }
}

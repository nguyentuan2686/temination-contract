package com.example.demo.micserver.response;

import lombok.Data;

@Data
public class UploadFileResponse {
  private String id;
  private String name;
  private String legacyName;
  private String insuredId;
  private String type;
}

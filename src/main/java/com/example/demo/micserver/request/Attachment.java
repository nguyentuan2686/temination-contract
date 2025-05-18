package com.example.demo.micserver.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {
    private String id;
    private String name;
    private String legacyName;
    private Boolean isUploadNew;
    private String fileName;
    private String nodeId;
    private String insuredId;
    private String type;
}


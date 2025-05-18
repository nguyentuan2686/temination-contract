package com.example.demo.micserver.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildTermResponse {
    private String contractId;
    private String insuredId;
    private String policyId;
    private String policyNo;
    private String currentContractValue;
    private String receivedAmount;
}

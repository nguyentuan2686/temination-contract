package com.example.demo.micserver.response;

import lombok.Data;

@Data
public class TerminationResponse {
    private String id;
    private String rootId;
    private String rootPolicyNo;
    private String cusName;
    private String cusTax;
    private String cusCoreCode;
    private String cusCode;
    private String branchId;
    private String branchName;
    private String departmentCode;
    private String departmentName;
    private String distributionChannel;
    private String distributionChannelName;
    private String distributionCode;
    private String distributionName;
    private String currentContractValue;
    private String totalReceivedAmount;
    private String policyStartDate;
    private String policyEndDate;
    private String policyApproveDate;
    private String managerialStaff;
    private String currencyType;
    private String contractStatus;
    private String contractStatusName;
    private String requestType;
    private String requestTypeName;
    private String terminationDate;
    private String currencySumInsured;
    private String refundAmount;
    private String cusCodeAndName;
}

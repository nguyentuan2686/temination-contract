package com.example.demo.micserver.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractContent {
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
    private String policyStartDate;
    private String policyEndDate;
    private String policyApproveDate;
    private String managerialStaff;
    private String currencyType;
    private String contractStatus;
    private String contractStatusName;
    private List<ChildTermResponse> listChildTermResponse;
}

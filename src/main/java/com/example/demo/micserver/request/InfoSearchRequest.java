package com.example.demo.micserver.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InfoSearchRequest {
    private String branchId;
    private List<String> departmentCodes;
    private String distributionChannel;
    private String startDate;
    private String endDate;
    private String searchByCustomer;
    private String policyNo;
    private List<String> listContractStatus;
    private List<String> managerialStaffs;
    private String searchType;
    private int page;
    private int size;
    private String businessTypeCode;
    private String distributionSearch;
}

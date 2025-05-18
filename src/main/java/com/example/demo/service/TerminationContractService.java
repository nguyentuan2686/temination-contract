package com.example.demo.service;

import com.example.demo.entity.Result;
import com.example.demo.filter.TokenHolder;
import com.example.demo.micserver.AlfrescoUploadClient;
import com.example.demo.micserver.DetailContractClient;
import com.example.demo.micserver.GetInsInfoClient;
import com.example.demo.micserver.TerminationContractClient;
import com.example.demo.micserver.request.*;
import com.example.demo.micserver.response.ContractContent;
import com.example.demo.micserver.response.DetailContractResponse;
import com.example.demo.micserver.response.UploadFileResponse;
import com.example.demo.ulti.DateTimeUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TerminationContractService {
  private final GetInsInfoClient getInsInfoClient;
  private final TerminationContractClient terminationContractClient;
  private final DetailContractClient detailContractClient;
  private final ExcelService excelService;
  private final AlfrescoUploadClient uploadClient;

  public Result terminationContract(MultipartFile file) {
    Result result = new Result();
    result.setListNoContract(new ArrayList<>());
    result.setListError(new ArrayList<>());
    result.setListDone(new ArrayList<>());
    try {
      String token = TokenHolder.getToken();
      if (token == null) {
        return null;
      }
      List<String> codes;
      try {
        codes = excelService.readCodes(file.getInputStream());
      } catch (Exception e) {
        return null;
      }
      if (CollectionUtils.isEmpty(codes)) {
        return null;
      }

      result.setTotal(codes.size());

      codes.forEach(s -> {
        callTerminationContract(token, result, s);
      });
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return result;
  }

  private InfoSearchRequest buildInfoSearchRequest(String policy) {
    return InfoSearchRequest.builder()
        .branchId("")
        .departmentCodes(new ArrayList<>())
        .distributionChannel("")
        .startDate(null)
        .endDate(DateTimeUtils.getNowInOffsetFormat(LocalDateTime.now()))
        .searchByCustomer("")
        .policyNo(policy)
        .listContractStatus(new ArrayList<>())
        .managerialStaffs(new ArrayList<>())
        .searchType("normal")
        .page(0)
        .size(10)
        .businessTypeCode("")
        .distributionSearch("")
        .build();
  }


  private void callTerminationContract(String token, Result result, String policy) {
    if (token == null || policy == null) {
      return;
    }
    InfoSearchRequest infoSearchRequest = buildInfoSearchRequest(policy);
    List<ContractContent> contractContents = getInsInfoClient.callSearchContract(infoSearchRequest, token, result);
    if (!CollectionUtils.isEmpty(contractContents)) {
      for (ContractContent contractContent : contractContents) {
        DetailContractResponse detailContract = detailContractClient.getDetailContract(contractContent.getId(), policy, token, result);
        UploadFileResponse uploadFileResponse = uploadClient.uploadFileToAlfresco(token, policy, result);
        TerminationContractRequest terRequest = toTerminationContractRequest(detailContract, uploadFileResponse);
        terminationContractClient.callTerminationContract(token, terRequest, result);
      }
    }
  }

  private TerminationContractRequest toTerminationContractRequest(DetailContractResponse detail, UploadFileResponse fileResponse) {
    return TerminationContractRequest.builder()
        .policyNo(detail.getRootPolicyNo())
        .terminationDate(detail.getTerminationDate())
        .cusName(detail.getCusName())
        .policyStartDate(detail.getPolicyStartDate())
        .policyEndDate(detail.getPolicyEndDate())
        .remainingDate(detail.getRemainingDate())
        .currencyType(detail.getCurrencyType())
        .reasonCode("CD00002")
        .reasonName("CD00002 - Chấm dứt thực hiện HĐBH theo thoả thuận (Được quy định tại HĐBH mẫu)")
        .requestType("C")
        .requester("K")
        .feeRefundRequest(false)
        .taxRefundRequest(false)
        .cusType(detail.getCusType())
        .negotiableRate(StringUtils.isEmpty(detail.getNegotiableRate()) ? "1" : detail.getNegotiableRate())
        .invoiceIssuance(true)
        .note("")
        .taxAmount(0)
        .payAmount(0)
        .exchangeRate(detail.getExchangeRate())
        .payAmountConvert(0)
        .taxAmountConvert(0)
        .premiums("")
        .cusTax(detail.getCusTax())
        .cusEmail(StringUtils.isEmpty(detail.getCusEmail()) ? "baohiemnet247@gmail.com" : detail.getCusEmail())
        .cusAddress(detail.getCusAddress())
        .issuanceInvoiceName(detail.getCusName())
        .issuanceInvoiceAddress(detail.getCusAddress())
        .issuanceInvoiceTax(detail.getCusTax())
        .executionDate("")
        .invoiceType(detail.getInvoiceType())
        .feeParents(new ArrayList<>())
        .currencyTypePay(detail.getCurrencyType())
        .policyCurrencyType(detail.getCurrencyType())
        .editRate("R")
        .insuranceRefundFee(new InsuranceRefundFee(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()))
        .feeAttachment(new FeeAttachment(List.of(buildAttachment(fileResponse))))
        .rootId(detail.getRootId())
        .rootPolicyNo(detail.getRootPolicyNo())
        .contractId(detail.getContractId())
        .insuranceRefund(detail.getInsuranceRefund())
        .compensationRate(StringUtils.isEmpty(detail.getCompensationRate()) ? "0" : detail.getCompensationRate())
        .compensationReceived(detail.getCompensationReceived())
        .dataCustomer(detail.getDataCustomer())
        .contractRate(detail.getContractRate())
        .distributionType(detail.getDistributionType())
        .distributionChannel(detail.getDistributionChannel())
        .distributionCode(detail.getDistributionCode())
        .cfgTermRecommendAmount(detail.getCfgTermRecommendAmount())
        .refundConfigured(detail.getRefundConfigured())
        .cusEmails(new ArrayList<>())
        .viewType(new ArrayList<>())
        .build();
  }

  private Attachment buildAttachment(UploadFileResponse fileResponse) {
    return Attachment.builder()
        .id(fileResponse.getId())
        .fileName(fileResponse.getName())
        .legacyName(fileResponse.getLegacyName())
        .name(fileResponse.getName())
        .isUploadNew(true)
        .nodeId(fileResponse.getId())
        .insuredId(fileResponse.getInsuredId())
        .type(fileResponse.getType())
        .build();
  }
}

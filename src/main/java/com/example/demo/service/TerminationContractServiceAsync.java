package com.example.demo.service;

import com.example.demo.entity.Result;
import com.example.demo.filter.TokenHolder;
import com.example.demo.micserver.*;
import com.example.demo.micserver.request.*;
import com.example.demo.micserver.response.ContractContent;
import com.example.demo.micserver.response.DetailContractResponse;
import com.example.demo.micserver.response.UploadFileResponse;
import com.example.demo.ulti.DateTimeUtils;
import com.example.demo.ulti.TokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TerminationContractServiceAsync {
  private final GetInsInfoClient getInsInfoClient;
  private final TerminationContractClient terminationContractClient;
  private final DetailContractClient detailContractClient;
  private final ExcelService excelService;
  private final AlfrescoUploadClient uploadClient;
  private final TokenUtils tokenUtils;

  public Result terminationContract(MultipartFile file) {
    Result result = new Result();
    result.setListNoContract(Collections.synchronizedList(new ArrayList<>()));
    result.setListError(Collections.synchronizedList(new ArrayList<>()));
    result.setListDone(Collections.synchronizedList(new ArrayList<>()));

    try {
      List<String> codes = excelService.readCodes(file.getInputStream());

      if (CollectionUtils.isEmpty(codes)) {
        log.info("No codes found");
        return null;
      }

      result.setTotal(codes.size());

      callTerminationContract(TokenHolder.getToken(), result, codes);
    } catch (Exception e) {
      log.error("terminationContract, read excel error", e);
    }
    return result;
  }

  private void callTerminationContract(String token, Result result, List<String> codes) {
    List<DetailContractResponse> detailContractResponses = fetchContractDetails(token, codes, result);

    if(CollectionUtils.isEmpty(detailContractResponses)) {
      log.info("No detail contract found");
      return;
    }

    log.info("Process with total: {}", detailContractResponses.size());

    detailContractResponses.forEach(detail -> {
      log.info("policy detail process: {}", detail.getRootPolicyNo());
    });

    tokenUtils.renewToken();
    token = TokenHolder.getToken();

    List<UploadFileResponse> uploadFileResponses = uploadFiles(token, detailContractResponses.size());
    if(!CollectionUtils.isEmpty(uploadFileResponses)) {
      try {
        for (int i = 0; i< detailContractResponses.size(); i++) {
          detailContractResponses.get(i).setUploadFileResponse(uploadFileResponses.get(i));
        }
      } catch (Exception e) {
        log.info("uploadFileResponses, uploadFileResponses error", e);
      }
    }

    terminateContracts(detailContractResponses, result, token);
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
    if(fileResponse == null) {
      return Attachment.builder()
          .id("016d14f5-abd8-48f9-9b4b-75d56f8829cc")
          .fileName("huy.pdf")
          .legacyName("huy.pdf")
          .isUploadNew(false)
          .build();
    }
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

  private List<DetailContractResponse> fetchContractDetails(String token, List<String> codes, Result result) {
    List<CompletableFuture<List<DetailContractResponse>>> futures = codes.stream()
        .map(code -> CompletableFuture.supplyAsync(() -> searchContracts(token, code, result))
            .thenCompose(contents -> {
              if (contents == null || contents.isEmpty()) {
                result.getListNoContract().add(code);
                return CompletableFuture.completedFuture(Collections.emptyList());
              }
              return fetchDetails(token, code, contents, result);
            })
        ).toList();

    return futures.stream()
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<ContractContent> searchContracts(String token, String policy, Result result) {
    InfoSearchRequest infoSearchRequest = buildInfoSearchRequest(policy);
    return getInsInfoClient.callSearchContract(infoSearchRequest, token, result);
  }

  private CompletableFuture<List<DetailContractResponse>> fetchDetails(String token, String policy,
                                                                       List<ContractContent> contractContents,
                                                                       Result result) {
    if (CollectionUtils.isEmpty(contractContents)) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }

    List<CompletableFuture<DetailContractResponse>> detailFutures = contractContents.stream().map(contractContent ->
        CompletableFuture.supplyAsync(() -> detailContractClient.getDetailContract(contractContent.getId(), policy, token, result))).toList();

    return CompletableFuture.allOf(detailFutures.toArray(new CompletableFuture[0]))
        .thenApply(v -> detailFutures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList()));
  }

  private List<UploadFileResponse> uploadFiles(String token, int size) {
    List<UploadFileResponse> uploadedDetails = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      UploadFileResponse uploadFileResponse = uploadClient.uploadFileToAlfresco(token);
      if (Objects.nonNull(uploadFileResponse)) {
        uploadedDetails.add(uploadFileResponse);
      }
    }

    return uploadedDetails;
  }

  private void terminateContracts(List<DetailContractResponse> contractDetails, Result result, String token) {
    List<CompletableFuture<Void>> terminateFutures = contractDetails.stream()
        .map(detail -> CompletableFuture.runAsync(() -> {
          try {
            TerminationContractRequest req = toTerminationContractRequest(detail, detail.getUploadFileResponse());
            terminationContractClient.callTerminationContract(token, req, result);
          } catch (Exception e) {
            result.getListError().add(detail.getRootPolicyNo());
          }
        })).toList();
    terminateFutures.forEach(CompletableFuture::join);
  }
}

package com.example.demo.service;

import com.example.demo.entity.Result;
import com.example.demo.filter.TokenHolder;
import com.example.demo.micserver.GetInsInfoClient;
import com.example.demo.micserver.request.InfoSearchRequest;
import com.example.demo.micserver.response.ContractContent;
import com.example.demo.ulti.DateTimeUtils;
import com.example.demo.ulti.TokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SearchContractService {
  private final ExcelService excelService;

  private final GetInsInfoClient getInsInfoClient;

  private final TokenUtils tokenUtils;

  public List<String> searchContractAsync(MultipartFile file) throws IOException {
    List<String> codes = excelService.readCodes(file.getInputStream());

    log.info("codes size: {}", codes.size());
    if (CollectionUtils.isEmpty(codes)) {
      return null;
    }

    tokenUtils.renewToken();

    String token = TokenHolder.getToken();

    List<CompletableFuture<String>> futures = codes.stream()
        .map(code -> CompletableFuture.supplyAsync(() -> {
          try {
            List<ContractContent> contracts = searchContracts(token, code);
            if (!CollectionUtils.isEmpty(contracts)) {
              return code;
            }
          } catch (Exception e) {
            log.error("Failed to search contract for code {}: {}", code, e.getMessage(), e);
          }
          return null;
        }))
        .toList();

    return futures.stream()
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public List<String> searchContract(MultipartFile file) throws IOException {
    List<String> codes = excelService.readCodes(file.getInputStream());

    log.info("codes size: {}", codes.size());
    if (CollectionUtils.isEmpty(codes)) {
      return null;
    }

    tokenUtils.renewToken();

    List<String> contractCodes = new ArrayList<>();
    codes.forEach(code -> {
      List<ContractContent> contractContents = searchContracts(TokenHolder.getToken(), code);
      if (!CollectionUtils.isEmpty(contractContents)) {
        contractCodes.add(code);
      }
    });

    return contractCodes;
  }


  private List<ContractContent> searchContracts(String token, String policy) {
    InfoSearchRequest infoSearchRequest = buildInfoSearchRequest(policy);
    return getInsInfoClient.callSearchContract(infoSearchRequest, token, new Result());
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


}

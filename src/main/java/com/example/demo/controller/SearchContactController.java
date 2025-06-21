package com.example.demo.controller;

import com.example.demo.service.SearchContractService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SearchContactController {

  private final SearchContractService searchContractService;

  @PostMapping(value = "/search-contract-async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<String>> searchOrdersAsync(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseEntity.ok(searchContractService.searchContractAsync(file));
  }

  @PostMapping(value = "/search-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<String>> searchOrders(@RequestParam("file") MultipartFile file) throws IOException {
    return ResponseEntity.ok(searchContractService.searchContract(file));
  }
}

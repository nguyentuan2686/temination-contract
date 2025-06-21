package com.example.demo.controller;

import com.example.demo.entity.Result;
import com.example.demo.service.TerminationContractService;
import com.example.demo.service.TerminationContractServiceAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/automation")
public class AutoController {
    @Autowired
    private TerminationContractServiceAsync terminationContractServiceAsync;

    @Autowired
    private TerminationContractService terminationContractService;

    @PostMapping(value = "/cancel-orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result> cancelOrders(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(terminationContractService.terminationContract(file));
    }

    @PostMapping(value = "/cancel-orders-async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result> cancelOrdersAsync(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(terminationContractServiceAsync.terminationContract(file));
    }
}

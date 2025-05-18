package com.example.demo.micserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceRefundFee {
    private List<Object> feesRefund;
    private List<Object> insuranceFees;
    private List<Object> compensationFees;
}


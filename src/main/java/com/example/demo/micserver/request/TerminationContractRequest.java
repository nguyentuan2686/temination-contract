package com.example.demo.micserver.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TerminationContractRequest {
    private String policyNo;
    private String terminationDate;
    private String cusName;
    private String insuredPerson;
    private String policyStartDate;
    private String policyEndDate;
    private String remainingDate;
    private String currencyType;
    private String reasonCode;
    private String reasonName;
    private String requestType;
    private String requester;
    private boolean feeRefundRequest;
    private boolean taxRefundRequest;
    private String cusType;
    private boolean invoiceIssuance;
    private String negotiableRate;
    private int taxAmount;
    private int payAmount;
    private int exchangeRate;
    private int payAmountConvert;
    private int taxAmountConvert;
    private String premiums;
    private String cusTax;
    private String cusEmail;
    private String cusAddress;
    private String issuanceInvoiceName;
    private String issuanceInvoiceAddress;
    private String issuanceInvoiceTax;
    private String executionDate;
    private String currencyTypePay;
    private String policyCurrencyType;
    private String editRate;
    private String rootId;
    private String rootPolicyNo;
    private String contractId;
    private boolean insuranceRefund;
    private boolean compensationReceived;
    private boolean dataCustomer;
    private String contractRate;
    private String distributionType;
    private String distributionChannel;
    private String distributionCode;
    private boolean cfgTermRecommendAmount;
    private boolean refundConfigured;

    private InsuranceRefundFee insuranceRefundFee;
    private FeeAttachment feeAttachment;
    private String note;
    private String invoiceType;
    private List<String> feeParents;
    private String issuedCurrencyType;
    private String payOption;
    private String invoiceReceive;
    private String coInsuredInvoice;
    private String compensationRate;
    private List<String> cusEmails;
    private List<String> viewType;
}

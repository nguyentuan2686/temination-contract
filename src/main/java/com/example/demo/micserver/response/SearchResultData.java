package com.example.demo.micserver.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultData {
    private int totalPages;
    private int totalElements;
    private int currentPage;
    private List<ContractContent> content;

    // Getters và Setters
}

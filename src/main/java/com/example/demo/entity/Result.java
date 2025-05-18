package com.example.demo.entity;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private List<String> listDone;
    private List<String> listError;
    private List<String> listNoContract;
    private Integer total;
}

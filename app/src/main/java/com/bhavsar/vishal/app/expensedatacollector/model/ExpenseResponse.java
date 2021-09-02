package com.bhavsar.vishal.app.expensedatacollector.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExpenseResponse {
    private ExpenseRecord record;
    private int code;
    private String message;
}

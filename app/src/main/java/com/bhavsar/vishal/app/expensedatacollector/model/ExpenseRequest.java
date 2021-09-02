package com.bhavsar.vishal.app.expensedatacollector.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExpenseRequest {
    private ExpenseRecord requestBody;
    private Map<String, String> headers;
}

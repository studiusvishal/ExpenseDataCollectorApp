package com.bhavsar.vishal.app.expensedatacollector.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExpenseRecord {
    private long id;
    private String expenseDate;
    private String description;
    private String category;
    private double expenseAmount;
}

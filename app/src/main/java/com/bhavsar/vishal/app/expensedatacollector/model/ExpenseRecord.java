package com.bhavsar.vishal.app.expensedatacollector.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExpenseRecord {
    private long id;
    private Date expenseDate;
    private String description;
    private String category;
    private double expenseAmount;
}

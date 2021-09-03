package com.bhavsar.vishal.app.expensedatacollector.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryRecord {
    private long id;
    private String name;
}

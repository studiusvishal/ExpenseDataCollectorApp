package com.bhavsar.vishal.app.expensedatacollector.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRecord {
    private String username;
    private String password;
    private long id;
}

package com.bhavsar.vishal.app.expensedatacollector.model;

import com.bhavsar.vishal.app.expensedatacollector.callbacks.GetCategoriesCallback;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryRequest {
    private GetCategoriesCallback.ResponseListener responseListener;
    private GetCategoriesCallback.ErrorListener errorListener;
}

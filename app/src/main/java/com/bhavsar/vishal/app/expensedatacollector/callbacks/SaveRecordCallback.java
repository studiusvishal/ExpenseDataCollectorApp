package com.bhavsar.vishal.app.expensedatacollector.callbacks;

import org.json.JSONObject;

public interface SaveRecordCallback {
    void onSaveSuccess(final JSONObject recordId);
}

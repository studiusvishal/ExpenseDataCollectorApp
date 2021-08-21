package com.bhavsar.vishal.app.expensedatacollector.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtility {

    public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";

    public String getCurrentDate() {
        return getDateFormat().format(new Date());
    }

    public DateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
    }
}

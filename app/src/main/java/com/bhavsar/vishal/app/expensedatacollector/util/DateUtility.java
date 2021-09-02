package com.bhavsar.vishal.app.expensedatacollector.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class DateUtility {

    public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";

    public String getCurrentDate() {
        return getDateFormat().format(new Date());
    }

    public DateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
    }

    @SneakyThrows
    public static Date parseDate(final String stringDate) {
        return getDateFormat().parse(stringDate);
    }

    public static String getDateFromCalendarSelection(final Long selection) {
        // https://stackoverflow.com/questions/58931051/materialdatepicker-get-selected-dates
        // user has selected a date
        // format the date and set the text of the input box to be the selected date
        // right now this format is hard-coded, this will change
        // Get the offset from our timezone and UTC.
        val timeZoneUTC = TimeZone.getDefault();
        // It will be negative, so that's the -1
        val offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        // Create a date format, then a date object with our offset
        val simpleFormat = getDateFormat();
        val date = new Date(selection + offsetFromUTC);
        return simpleFormat.format(date);
    }
}

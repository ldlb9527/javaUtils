package com.ldlb.time;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateTimeTest {


    @Test
    public void testDiff() {
        DateTime dateTime = DateTime.now().moveInYear(0, 1, 0);
        System.out.println(dateTime.ISODateTime());
        System.out.println(dateTime.toUtilDate());
    }
}

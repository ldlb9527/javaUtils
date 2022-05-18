package com.ldlb.time;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateTimeTest {


    @Test
    public void test() throws Exception {
        String s = DateTime.beginOfDay(2020, 1, 1).ISODateTime();
        System.out.println(s);

    }
}

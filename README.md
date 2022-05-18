![](https://img.shields.io/badge/jdk1.8%2B-javaUtils-green)
# Sample
## 1.DateTime
***
### 获取当前时间(2022-05-18)
***
```
String dateTime = DateTime.today().ISODateTimeMs();  ##  获取当天零点时间 2022-05-18 00:00:00

int year = DateTime.now().getYear();                 ##  2022
int month = DateTime.now().getMonth();               ##  5
int day = DateTime.now().getDate();                  ##  18

String dateTime = DateTime.now().ISODateTime();      ##  2022-05-18 13:37:14

String dateTime = DateTime.now().ISODate();          ##  2022-05-18

String dateTime = DateTime.now().ISODateTimeMs();    ##  2022-05-18 13:46:47.684

int time = DateTime.now().YYYYMMDD();                ##  20220518

Date date = DateTime.now().toUtilDate();             ##  Wed May 18 13:54:32 CST 2022

```
***
### 时间偏移(2022-05-18)
* `DateTime moveInYear(int y, int m, int d)` 入参分别代表年、月、日, 正数加负数减
* `DateTime moveInDay(int h, int m, int s)`  入参分别代表时、钟、秒, 正数加负数减

```
String dateTime = DateTime.now().moveInYear(1, 0, -1).ISODateTime();                       //获取昨天的明年的时间2023-05-17 14:22:40

String dateTime = DateTime.now().moveInDay(-1, 0, 0).ISODateTime();                        //获取1小时前的时间2022-05-18 13:24:00

String dateTime = DateTime.now().moveInYear(1, 0, -1).moveInDay(-1, 0, 0).ISODateTime();   //获取昨天的明年的1小时前的时间 2023-05-17 13:25:40
```
***
### 时间比较
* `boolean isAfter(DateTime oth)` 判断时间是否比入参时间大
* `boolean isBefore(DateTime oth)` 判断时间是否比入参时间小
```java
public class DateTimeTest {
    @Test
    public void test() throws Exception {
        DateTime start = DateTime.now();
        Thread.sleep(1);
        DateTime end = DateTime.now();

        System.out.println(start.isAfter(end));  // false
        System.out.println(start.isBefore(end)); // true
    }
}
```
* `diffMs(DateTime from, DateTime to)` to减去from 返回毫秒级差值,提供多种重载方法
```java
public class DateTimeTest {
    @Test
    public void test() throws Exception {
        DateTime start = DateTime.now();
        Thread.sleep(1000);
        DateTime end = DateTime.now();

        System.out.println(DateTime.diffMs(end, start));  // -1016
    }
}
```
***
### 字符串转时间
* `DateTime parseString(String value)` value支持多种类型
```
String dateTime = DateTime.parseString("2021-09-30").ISODateTime();   ## 2021-09-30 00:00:00

String dateTime = DateTime.parseString("2021-09-30 01:00:05").ISODateTime();  ## 2021-09-30 01:00:05

String dateTime = DateTime.parseString("2021-09-30 01:00:05.258").ISODateTimeMs();  ##  2021-09-30 01:00:05.258

String dateTime = DateTime.parseString("20210930").ISODateTimeMs();              ##  2021-09-30 00:00:00.000
```
* `DateTime parseFormat(String value, String format)` 采用SimpleDateFormat实现
***

## 2.SegmentLock
## 3.KeyLock
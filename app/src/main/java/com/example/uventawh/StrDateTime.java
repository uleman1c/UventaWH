package com.example.uventawh;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StrDateTime {

    public static String strToDate(String str){

        String date = "";
        try {

            date = str.substring(6, 8) + "." + str.substring(4, 6) + "." + str.substring(0, 4);
        } catch (Exception e){

        }
        finally {

        }

        return date;

    }

    public static String strToTime(String str){


        String date = "";
        try {

            date = str.substring(8, 10) + ":" + str.substring(10, 12) + ":" + str.substring(12, 14);
        } catch (Exception e) {

        }finally {

        }

        return date;


    }

    public static String strToTimeWoS(String str){


        String date = "";
        try {

            date = str.substring(8, 10) + ":" + str.substring(10, 12);
        } catch (Exception e) {

        }finally {

        }

        return date;


    }

    public static String dateToStr(Date date){

        String res = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            res = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

    public static String dateToStrMs(Date date){

        String res = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            res = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

    public static String fotmatDateToStrDate(Date date){

        String res = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            res = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

    public static Date dateStrToDate(String str){

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;

    }

    public static String descTransportToString(String startDescTransport){

        String descTransport = startDescTransport.toUpperCase();

        return descTransport.substring(0, 1) + " " + descTransport.substring(1, 4) + " " + descTransport.substring(4, 6) + " " + descTransport.substring(6);

    }

    public static CharSequence descTransportToString(CharSequence startDescTransport){

        return startDescTransport.subSequence(0, 1) + " " + startDescTransport.subSequence(1, 4) + " " + startDescTransport.subSequence(4, 6) + " " + startDescTransport.subSequence(6, startDescTransport.length());

    }
}

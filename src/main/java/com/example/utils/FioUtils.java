package com.example.utils;

public class FioUtils {

    public static String getShortFio(String info) {
        info = info.substring(info.lastIndexOf(',') + 2);
        String[] fio = info.split(" ");
        if (fio.length == 2) {
            return fio[0] + " " + fio[1].charAt(0) + ".";
        } else {
            return fio[0] + " " + fio[1].charAt(0) + ". " + fio[2].charAt(0) + ".";
        }
    }
}

package com.example.yuryKorabel.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

//это вспомогательный класс для работы с ДАТОЙ
public class DateOperations {

    //этот метод вернет ДАТУ на вчерашний день в нужном формате
    public static String getYesterdayDateInFormat(String pattern){
        //создаем объект Календаря
        Calendar c = new GregorianCalendar();
        //получаем вчерашнюю дату
        c.add(Calendar.DATE, -1);
        //задаем нужный нам формат даты
        SimpleDateFormat myFormat = new SimpleDateFormat(pattern);
        //возвращаем вчерашнюю дату в нужном формате
        return myFormat.format(c.getTime());
    }
}

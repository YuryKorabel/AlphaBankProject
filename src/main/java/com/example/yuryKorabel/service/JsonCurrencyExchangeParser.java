package com.example.yuryKorabel.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//это вспомогательный класс для парсинга JSON-а
public class JsonCurrencyExchangeParser {

    //данный статический метод вытаскивает из JSON строки курс нужной нам валюты. Строка и код валюты передаются в параметре
    public static Double getRate(String jsonString, String currencyCode) throws ParseException {
        // Считываем json
        Object obj = new JSONParser().parse(jsonString);
        // Кастим obj в JSONObject
        JSONObject jo = (JSONObject) obj;
        // Достаем объект, содержащий все курсы валют, это будет снова JSONObject
        JSONObject allCurrency = (JSONObject) jo.get("rates");
        //достаем значение курса заданной валюты
        Double rate = (Double) allCurrency.get(currencyCode);

        return rate;
    }


}

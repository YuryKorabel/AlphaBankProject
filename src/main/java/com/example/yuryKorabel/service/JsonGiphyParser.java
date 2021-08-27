package com.example.yuryKorabel.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//этот вспомогательный класс создан для парсинга JSON строки в ответе от сервиси ГИФОК
public class JsonGiphyParser {

    //этот статический метод вернет ссылку на гифку. Ссылка находится в строке ответа сервиса ГИФОК.
    //кроме ссылки эта строка содержит много мусора, поэтому нужно отдельно с ней поработать в этом методе
    public static String getGiphyURL(String jsonString) throws ParseException {
        // Считываем json
        Object obj = new JSONParser().parse(jsonString);
        // Кастим obj в JSONObject
        JSONObject jo = (JSONObject) obj;
        // Достаем внутренний объект data
        JSONObject allCurrency = (JSONObject) jo.get("data");
        //достаем из поля объекта строку, содержащую ссылку на гифку
        String url = (String) allCurrency.get("image_url");
        return url;
    }

}

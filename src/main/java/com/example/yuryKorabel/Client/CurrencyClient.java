package com.example.yuryKorabel.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//Этот Клиент создан для общения с сервисов курсов Валют
@FeignClient(
        value="currencyClient",
        url = "${targetURL}"
)
public interface CurrencyClient {

    //данный метод будет обращаться к сервису курса валют и запрашивать курс на СЕГОДНЯ
    @RequestMapping(method = RequestMethod.GET, value="${currentCurrencyExchangeRateURL}")
    String getCurrentExchangeRate(@RequestParam("app_id") String app_id, @RequestParam("base") String base);

    //данный метод будет обращаться к сервису курса валют и запрашивать курс на дату в переменной yesterday
    @RequestMapping(method = RequestMethod.GET, value="${historicalCurrencyExchangeRateURL}"+"/{yesterday}")
    String getYesterdayExchangeRate(@PathVariable("yesterday") String yesterday, @RequestParam("app_id") String app_id, @RequestParam("base") String base);

}

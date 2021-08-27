package com.example.yuryKorabel.Controller;

import com.example.yuryKorabel.Client.CurrencyClient;
import com.example.yuryKorabel.Client.GiphyClient;

import com.example.yuryKorabel.service.DateOperations;
import com.example.yuryKorabel.service.JsonCurrencyExchangeParser;

import com.example.yuryKorabel.service.JsonGiphyParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@RestController
@RequestMapping("/getRate")
public class GetRateController {


    @Autowired
    private CurrencyClient currencyClient;

    @Autowired
    private GiphyClient giphyClient;

    @Value("${codeOfCurrencyThatWeHave}")
    private String codeOfCurrencyThatWeHave;

    @Value("${appID}")
    private String appID;

    @Value("${apiKeyForGiphyService}")
    private String apiKeyForGiphyService;

    @Value("${brokePictureCode}")
    private String brokePictureCode;

    @Value("${richPictureCode}")
    private String richPictureCode;

    @GetMapping
    public ResponseEntity getRates(HttpServletRequest request) {
        try {

            //зададим заголовки ответа. Пусть единственным заголовком будет тип данных в ответе.
            //именно этот тип мы будем ожидать и парсить в тестах
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html");

            //получим код валюты из запроса.
            String codeOfBenchmarkCurrency = request.getParameter("code");

            //проверим, что мы вообще получили CODE валюты в запросе. Если этого параметра в запросе нет, то вернем ответ с текстом ошибки
            if (Objects.isNull(codeOfBenchmarkCurrency)) {
                return new ResponseEntity<>(
                        "<html>" +
                                "<head>" +
                                    "<title>noCurrencyCodeFound</title>" +
                                "</head>" +
                                    "<body>there is no currency code in your request... please use request like /getRate?code=RUB</body>" +
                                "</html>",
                        headers, HttpStatus.BAD_REQUEST);
            }

            //с помощью вспомогательного объекта для работы с датой, получим строку, содержащую вчерашнюю дату в нужном нам формате
            String yesterday = DateOperations.getYesterdayDateInFormat("yyyy-MM-dd");
            //приведем строку к формату, который требуется веб сервисом Валют
            yesterday += ".json";

            //получим ответ от Сервиса Валют о состоянии курсов на СЕГОДНЯ
            String webServiceResponce = currencyClient.getCurrentExchangeRate(appID,codeOfCurrencyThatWeHave);
            //с помощью вспомогательного объекта-парсера получим курс нужной нам для сравнения валюты
            Double currentRate = JsonCurrencyExchangeParser.getRate(webServiceResponce,codeOfBenchmarkCurrency);

            //получим ответ от Сервиса Валют о состоянии курсов на ВЧЕРА
            webServiceResponce = currencyClient.getYesterdayExchangeRate(yesterday,appID,codeOfCurrencyThatWeHave);
            //с помощью вспомогательного объекта-парсера получим курс нужной нам для сравнения валюты
            Double yesterdayRate = JsonCurrencyExchangeParser.getRate(webServiceResponce,codeOfBenchmarkCurrency);

            //инициализируем ключевое слово для поиска картинки. Если сегодняшний курс выше чем вчерашний, то будем искать
            //картинку по слову richPictureCode, иначе по слову brokePictureCode
            String tag = richPictureCode;
            if (currentRate < yesterdayRate) tag = brokePictureCode;

            //вызываем метод, обращающийся к сервису гифок и записываем ссылку на его объект-ответ в переменную
            webServiceResponce = giphyClient.getRandomGiphy(apiKeyForGiphyService,tag);
            //вытаскиваем из ответа нужную нам строку - ссылку на гифку
            String giphyUrl = JsonGiphyParser.getGiphyURL(webServiceResponce);

            //для отладки выводим всю имеющуюся на данный момент инфомацию в консоль
            System.out.println("курс сегодня = "+currentRate);
            System.out.println("курс вчера = "+ yesterdayRate);
            System.out.println("tag = "+ tag);
            System.out.println("ссылка на гифку = "+ giphyUrl);

            //возвращаем положительный ответ
            return new ResponseEntity<>(
                    "<div " +
                    "style='display: flex; flex-direction: column; justify-content: center; margin: 100px auto; font-size: 20; width:400'> " +
                        "<p style='text-align: center;'>today 1 unit of "+codeOfCurrencyThatWeHave+" costs "+currentRate+" "+codeOfBenchmarkCurrency+"</p> " +
                        "<p style='text-align: center;'>yesterday 1 unit of "+codeOfCurrencyThatWeHave+" cost "+yesterdayRate+" "+codeOfBenchmarkCurrency+"</p> " +
                        "<p style='text-align: center;'>the soul mood of "+codeOfCurrencyThatWeHave+" owner is like:</p>" +
                        "<div style='margin: 0 auto;'>" +
                            "<img width=350 src='" +giphyUrl+ "' alt='soul mood'> " +
                        "</div>" +
                    "</div>",
                    headers, HttpStatus.OK
            );

        } catch (Exception e) {

            //В этот блок мы можем попасть, если получим плохие ответы от сервисов, в которые мы обращаемся.
            //Например, эти ответы будет невозможно распарсить.

            //зададим заголовки нашего ответа. Пусть единственным заголовком будет тип данных в ответе.
            //именно этот тип мы будем ожидать и парсить в тестах
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html");

            return new ResponseEntity<>(
                       "<html>" +
                                "<head>" +
                                    "<title>someErrors</title>" +
                                "</head>" +
                                "<body>there are some Errors... please check your request and currency code</body>" +
                            "</html>",
                    headers, HttpStatus.BAD_REQUEST);
        }
    }
}

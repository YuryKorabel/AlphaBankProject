package com.example.yuryKorabel.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//Этот клиент создан для общения с сервисом ГИФОК
@FeignClient(
        value="giphyClient",
        url = "${giphyURL}"
)
public interface GiphyClient {

    //этот единственный метод будет запрашивать рандомную ГИФКУ по ключевому слову, которое передается в параметре tag
    @RequestMapping(method = RequestMethod.GET)
    String getRandomGiphy(@RequestParam("api_key") String api_key, @RequestParam("tag") String tag);
}

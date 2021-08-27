package com.example.yuryKorabel;

import com.example.yuryKorabel.Controller.GetRateController;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;


@SpringBootTest
class startApplicationTests {

	private static final int PORT = 8080;
	private static final String HOST = "openexchangerates.org/api";
	private static WireMockServer server= new WireMockServer(PORT);


//===========================================================================================
//тест на предмет адекватного окончания работы в случае, если мы не получим входной параметр - КОД валюты
	@Test
	public void TestGetEndPoint() throws URISyntaxException {
		RestAssured
				.given()
				.when()
				.get(new URI("http://localhost:8080/getRate"))
				//.get("/getRate")
				.then()
				.assertThat()
				.statusCode(400)
				.and()
				.contentType(ContentType.HTML)
				.body("html.head.title",equalTo("noCurrencyCodeFound"));
	}
//===========================================================================================


//===========================================================================================
//тест на предмет адекватного окончания работы в случае, если сторонний сервис ответ отказом...
	@Test
	public void TestBadAnswerFromOpenExchangeService() throws URISyntaxException {

		server.start();
		ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();

		//статус 403 - сервер понял запрос, но отказывается его авторизовывать. (например, мы прикрепили невалидный ТОКЕН)
		mockResponse.withStatus(403);
		WireMock.configureFor(HOST, PORT);
		WireMock.stubFor(WireMock.get("/latest.json").willReturn(mockResponse));

		//создадим МОК в виде ПСЕВДО-запроса
		MockHttpServletRequest request = new MockHttpServletRequest();
		//добавим ему GET параметр
		request.addParameter("code", "RUB");

		//запустим для теста наш класс и его главный метод-контроллер, который по сути и запускает всю прорамму
		GetRateController getRateController = new GetRateController();
		ResponseEntity response = getRateController.getRates(request);

		//освободим ресурсы от работающего сервера, закончим его работу
		if (server.isRunning()){
			server.shutdownServer();
		}

		//проверим, содержит ли тело ответа определнную подстроку, которая должна появится в ответе при нашем сценарии
		// "плохого" ответа сервера или в случае любых других непредвиденных ошибок...
		assertEquals(true,response.getBody().toString().contains("<title>someErrors</title>"));
	}
//===========================================================================================


}

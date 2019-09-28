package com.n26;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.n26.Controller.TransactionController;
import com.n26.Service.StatisticsService;

/**
 * @author MiguelAraCo
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TransactionController.class, secure = false)
@ContextConfiguration(classes = { Application.class })
public class TransactionsControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private StatisticsService statisticsService;

	@Test
	public void returns201WhenTransactionIsRegistered() throws Exception {
		Mockito.when(statisticsService.register(Mockito.any())).thenReturn(StatisticsService.RegisterResult.REGISTERED);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("" + "{" + "   \"amount\": 12.65,\n" + "   \"timestamp\": 1529822905186\n" + "}");

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 201, response.getStatus());
	}

	@Test
	public void returns204WhenTransactionIsDiscarded() throws Exception {
		Mockito.when(statisticsService.register(Mockito.any())).thenReturn(StatisticsService.RegisterResult.DISCARDED);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("" + "{" + "   \"amount\": 12.65,\n" + "   \"timestamp\": 1529822905186\n" + "}");

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 204, response.getStatus());
	}

	@Test
	public void returns400OnIllegalArgumentException() throws Exception {
		Mockito.when(statisticsService.register(Mockito.any())).thenThrow(new IllegalArgumentException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("" + "{" + "   \"amount\": 12.65,\n" + "   \"timestamp\": 1529822905186\n" + "}");

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 400, response.getStatus());
	}

	@Test
	public void returns400OnMissingProperties() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.contentType(MediaType.APPLICATION_JSON).content("" + "{" + "   \"amount\": 12.65\n" + "}");

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 400, response.getStatus());

		requestBuilder = MockMvcRequestBuilders.post("/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("" + "{" + "   \"timestamp\": 1529822905186\n" + "}");

		response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 400, response.getStatus());
	}

	@Test
	public void returns422OnUnParsableProperties() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.contentType(MediaType.APPLICATION_JSON).content("" + "{" + "   \"amount\": \"super-invalid-amount\",\n"
						+ "   \"timestamp\": 1529822905186\n" + "}");

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 422, response.getStatus());

		requestBuilder = MockMvcRequestBuilders.post("/transactions").contentType(MediaType.APPLICATION_JSON).content(
				"" + "{" + "   \"amount\": 12.65,\n" + "   \"timestamp\": \"super-invalid-timestamp\"\n" + "}");

		response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 422, response.getStatus());
	}
}

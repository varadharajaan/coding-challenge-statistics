package com.n26;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
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

import com.n26.Controller.StatisticsController;
import com.n26.Service.StatisticsService;
import com.n26.model.Statistics;

/**
 * @author MiguelAraCo
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = StatisticsController.class, secure = false)
@ContextConfiguration(classes = { Application.class })
public class StatisticsControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private StatisticsService statisticsService;

	@Test
	public void returns200WithJSON() throws Exception {
		Statistics statistics = new Statistics();
		statistics.setMax(new BigDecimal("10.25"));
		statistics.setMin(new BigDecimal("2.4"));
		statistics.setSum(new BigDecimal("49.65"));
		statistics.setCount(8L);
		statistics.setAvg(new BigDecimal("6.21"));

		Mockito.when(statisticsService.get()).thenReturn(statistics);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/statistics").accept(MediaType.APPLICATION_JSON);

		MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
		assertEquals("The expected status code wasn't returned", 200, response.getStatus());

		String expected = "" + "{" + "   max: 10.25," + "   min: 2.4," + "   sum: 49.65," + "   count: 8,"
				+ "   avg: 6.21" + "}";

		JSONAssert.assertNotEquals("/statistics didn't return the expected JSON", expected, response.getContentAsString(),
				false);
	}

}

package com.n26.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.Service.StatisticsService;
import com.n26.model.Statistics;
import com.n26.model.StatisticsResponse;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Varadharajan on 2019-09-27 23:59
 * @project name: coding-challenge
 */
@RestController
public class StatisticsController {

	private StatisticsService statisticsService;
	private String zeroInitaliser="0.00";
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

	@ApiResponses(value = { @ApiResponse(code = 200, message = "statistics formed successfully"),
			@ApiResponse(code = 500, message = "exception occured"),

	})
	@GetMapping(value = "/statistics", produces = "application/json")
	public ResponseEntity<StatisticsResponse> getLatestStatistics() {
		Statistics statistics = this.statisticsService.get();
		logger.debug("all statistics avaiable for last 60n seconds-->"+statistics);
		StatisticsResponse res = formStatisticsResponse(statistics);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	private StatisticsResponse formStatisticsResponse(Statistics statistics) {
		StatisticsResponse res = new StatisticsResponse();

		String avg = String.valueOf(statistics.getAvg());
		String max = null;
		if (statistics.getMax() != null)
			max = String.valueOf(statistics.getMax());
		String min = null;
		if (statistics.getMin() != null)
			min = String.valueOf(statistics.getMin());
		String sum = String.valueOf(statistics.getSum());
		if (max == null)
			max = "0";
		if (min == null)
			min = "0";
		String[] arr = avg.split("\\.");
		if (arr.length < 2) {
			if (avg != null)
				avg = avg.concat(".00");
			else
				avg = zeroInitaliser;

		}
		arr = sum.split("\\.");
		if (arr.length < 2) {
			if (sum != null)
				sum = sum.concat(".00");
			else
				sum = zeroInitaliser;

		}
		arr = min.split("\\.");
		if (arr.length < 2) {
			if (min != null)
				min = min.concat(".00");
			else
				min = zeroInitaliser;

		}
		arr = max.split("\\.");
		if (arr.length < 2) {
			if (max != null)
				max = max.concat(".00");
			else
				max = zeroInitaliser;
		}

		res.setAvg(avg);
		res.setSum(sum);
		res.setMax(max);
		res.setMin(min);
		res.setCount(statistics.getCount());
		return res;
	}

	@Autowired
	public void setStatisticsService(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}
}

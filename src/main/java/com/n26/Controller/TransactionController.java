package com.n26.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.n26.Service.StatisticsService;
import com.n26.model.Transaction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Varadharajan on 2019-09-27 23:58
 * @project name: coding-challenge
 */
@RestController
public class TransactionController {
	private StatisticsService statisticsService;
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@ApiOperation(value = "Create Transaction", notes = "Create Transaction record and store for 60 seconds")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created Successfully"),
			@ApiResponse(code = 204, message = "Past Value Found"),
			@ApiResponse(code = 400, message = "Invalid Json Input"),
			@ApiResponse(code = 404, message = "Value not found"), @ApiResponse(code = 500, message = "Server Error"),
			@ApiResponse(code = 422, message = "Unparasbale fioeld error"), })
	@PostMapping(value = "transactions", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Void> addTransaction(@Valid @RequestBody final Transaction transaction) {
		StatisticsService.RegisterResult result;

		try {

			result = this.statisticsService.register(transaction);
			logger.debug("inside try block checking for result if registered or to be discarded-->"+result);
		} catch (IllegalArgumentException e) {

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		switch (result) {
		case REGISTERED:
			return new ResponseEntity<>(HttpStatus.CREATED);
		case DISCARDED:
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		default:
			throw new IllegalStateException();
		}
	}

	@ApiOperation(value = "Delete Transaction", notes = "Delete Transaction record that stored for last 60 seconds")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Deleted Successfully"),
			@ApiResponse(code = 500, message = "Server Error"),

	})
	@DeleteMapping(value = "transactions")
	public ResponseEntity<Void> DeleteTransaction() {
		statisticsService.clear();
		logger.debug("all transactions deleted from storage");
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	public static boolean isValidDate(String pDateString) throws ParseException {
		Date date = new SimpleDateFormat("MM/dd/yyyy").parse(pDateString);
		return new Date().before(date);
	}

	@Autowired
	public void setStatisticsService(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}
}

package com.n26.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Varadharajan on 2019-09-27 23:54
 * @project name: coding-challenge
 */
public class Transaction {
	@NotNull(message = "amount cannot be null")
	private final BigDecimal amount;
	@NotNull(message = "timestamp is needed and cannot be null")
	private final Instant timestamp;

	@JsonCreator
	public Transaction(@JsonProperty("amount") BigDecimal amount, @JsonProperty("timestamp") Instant timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}
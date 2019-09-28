package com.n26.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Varadharajan on 2019-09-28 01:01
 * @project name: coding-challenge
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValueNotFoundException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6601130339967844171L;

	public ValueNotFoundException() {
		super();
	}

	public ValueNotFoundException(String message) {
		super(message);
	}

}

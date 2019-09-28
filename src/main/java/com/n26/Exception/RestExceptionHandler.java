package com.n26.Exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.n26.model.ExceptionResponse;

/**
 * @author Varadharajan on 2019-09-28 00:52
 * @project name: coding-challenge
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public final ResponseEntity<ExceptionResponse> handleInvalidArguments(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setStatus(HttpStatus.BAD_REQUEST);
		exceptionResponse.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST));
		exceptionResponse.setMessage(ex.getMessage());
		exceptionResponse.setDetails(request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValueNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleValueNotFoundException(ValueNotFoundException ex,
			WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setStatus(HttpStatus.BAD_REQUEST);
		exceptionResponse.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST));
		exceptionResponse.setMessage(ex.getMessage());
		exceptionResponse.setDetails(request.getDescription(false));
		if(ex.getMessage().contains("future"))
			return new ResponseEntity<>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle HttpMessageNotReadableException. Happens when request JSON is
	 * malformed.
	 *
	 * @param ex      HttpMessageNotReadableException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the exceptionResponse object
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		

		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
		exceptionResponse.setErrorCode(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY));
		exceptionResponse.setMessage(ex.getMessage());
		exceptionResponse.setDetails(request.getDescription(false));
		if(!(ex.getMessage().contains("amount")|| ex.getMessage().contains("timestamp")))
			return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	/**
	 * Handle HttpMessageNotWritableException.
	 *
	 * @param ex      HttpMessageNotWritableException
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ExceptionResponse object
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		exceptionResponse.setErrorCode("500");
		exceptionResponse.setMessage("Json output Error");
		exceptionResponse.setDetails(request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
	 * validation.
	 *
	 * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
	 *                validation fails
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the exceptionResponse object
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setStatus(HttpStatus.BAD_REQUEST);
		exceptionResponse.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST));
		exceptionResponse.setMessage("Json MalFormed");

		if (bindingResult.hasErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			List<String> message = new ArrayList<>();
			for (FieldError e : errors) {
				message.add("@" + e.getField().toUpperCase() + ":" + e.getDefaultMessage());
			}

			exceptionResponse.setDetails(message.toString());
		}
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed Method not supported: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed MediaType not supported: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed mediaType not acceptable: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed missing path variable: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed missing Parameter: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed binding: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed convertion: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed TypeMismatch: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed missing servlet Request part: ",
				ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed bind: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed noHandler: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
			HttpHeaders headers, HttpStatus status, WebRequest webRequest) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed RequestTimeout: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ExceptionResponse errorDetails = new ExceptionResponse("Failed InternalException: ", ex.getMessage());
		return new ResponseEntity<>(errorDetails, status);
	}
}

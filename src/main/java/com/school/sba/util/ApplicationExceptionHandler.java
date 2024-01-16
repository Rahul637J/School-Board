package com.school.sba.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.UserNotFoundException;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler
{
	
	@Autowired
	ExceptionResponse<String> structure;
	
	private ResponseEntity<Object> error(HttpStatus status,String message, Object rootcause)
	{
		return new ResponseEntity<Object> (Map.of(
				"status",status.value(),
				"message",message,
				"rootcause",rootcause),status);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<ObjectError> allErrors = ex.getAllErrors();
		Map<String, String> map=new HashMap<String, String>();
		allErrors.forEach(errors->{
			FieldError fieldError=(FieldError)errors;
			map.put(fieldError.getField(), fieldError.getDefaultMessage());
		});
		return error(HttpStatus.BAD_REQUEST,"Bad Request", ex.getMessage());//"UserName or email cannot be null or empty"
	}
	
	@ExceptionHandler(DuplicateEntryException.class)
	public ResponseEntity<ExceptionResponse<String>> duplicteAdmin(DuplicateEntryException dex)
	{
		error(HttpStatus.NOT_FOUND, dex.getMessage(), "The Admin is already Exist");
		return new ResponseEntity<ExceptionResponse<String>>(structure,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<ExceptionResponse<String>> invalidUser(InvalidUserException unf)
	{
		error(HttpStatus.NOT_FOUND, unf.getMessage(), "The Admin is already Exist");
		return new ResponseEntity<ExceptionResponse<String>>(structure,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ExceptionResponse<String>> userNotFound(InvalidUserException unf)
	{
		error(HttpStatus.NOT_FOUND, unf.getMessage(), "User Not Found");
		return new ResponseEntity<ExceptionResponse<String>>(structure,HttpStatus.NOT_FOUND);
	}
}

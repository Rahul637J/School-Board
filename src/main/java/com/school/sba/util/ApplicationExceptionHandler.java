package com.school.sba.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.exception.SubjectNotAddedException;
import com.school.sba.exception.UserNotFoundException;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

	private ResponseEntity<Object> error(HttpStatus status, String message, Object rootcause) {
		return new ResponseEntity<Object>(Map.of(
				"status", status.value(),
				"message", message,
				"rootcause", rootcause),
				status);
	}
	
	@ExceptionHandler(DuplicateEntryException.class)
	public ResponseEntity<Object> duplicateEntryException(DuplicateEntryException dex) {
		return error(HttpStatus.NOT_FOUND, dex.getMessage(), "Change the UserName or email");
	}

	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<Object> invalidUser(InvalidUserException unf) {
		return error(HttpStatus.NOT_FOUND, unf.getMessage(), "The Admin is already Exist");
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> userNotFound(UserNotFoundException unf) {
		return error(HttpStatus.NOT_FOUND, unf.getMessage(), "User Not Found");
	}

	@ExceptionHandler(IllegalRequestException.class)
	public ResponseEntity<Object> illegalRequestException(IllegalRequestException ex) {
		return error(HttpStatus.NOT_FOUND, ex.getMessage(), "User Has Not Access");
	}
	
	@ExceptionHandler(ScheduleNotFoundException.class)
	public ResponseEntity<Object> scheduleNotFound(ScheduleNotFoundException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Schedule is not Yet created");
	}
	
	@ExceptionHandler(AcademicProgramNotFoundException.class)
	public ResponseEntity<Object> academicProgramNotFound(AcademicProgramNotFoundException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Academic Program Not Found");
	}
	
	@ExceptionHandler(AcademicProgramNotFoundById.class)
	public ResponseEntity<Object> academicProgramNotFoundById(AcademicProgramNotFoundById snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Academic Program Not Found By Id ");
	}
	
	@ExceptionHandler(SchoolNotFound.class)
	public ResponseEntity<Object> schoolNotFound(SchoolNotFound snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"School Not Found");
	}
	
	@ExceptionHandler(SubjectNotAddedException.class)
	public ResponseEntity<Object> subjectNotPresent(SubjectNotAddedException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Subjects Not present in Database");
	}
}

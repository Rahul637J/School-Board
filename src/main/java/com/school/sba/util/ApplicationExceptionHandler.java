package com.school.sba.util;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.AdminCannotBeAssignedToAcademicException;
import com.school.sba.exception.ClassRoomNotFreeException;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.InvalidClassHourDuratioion;
import com.school.sba.exception.InvalidClassHourIdException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.InvalidUserRoleException;
import com.school.sba.exception.IrreleventTeacherException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.exception.SubjectNotAddedException;
import com.school.sba.exception.UserIsNotAnAdminException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.exception.UsersNotAssociatedWithAcademicProgram;

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
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Subjects not yet added to Database");
	}
	
	@ExceptionHandler(UserIsNotAnAdminException.class)
	public ResponseEntity<Object> userNotAdmin(UserIsNotAnAdminException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Subjects Not present in Database");
	}
	
	@ExceptionHandler(IrreleventTeacherException.class)
	public ResponseEntity<Object> irrelavntSubject(IrreleventTeacherException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Subjects Not present in Database");
	}
	
	@ExceptionHandler(AdminCannotBeAssignedToAcademicException.class)
	public ResponseEntity<Object> adminCannotToAcademicProgram(AdminCannotBeAssignedToAcademicException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Only Teachers and Students can be assigned to Academic Programs");
	}
	
	@ExceptionHandler(InvalidClassHourIdException.class)
	public ResponseEntity<Object> invalidClassHourException(InvalidClassHourIdException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Class Hour Id is not present");
	}
	
	@ExceptionHandler(InvalidUserRoleException.class)
	public ResponseEntity<Object> invalidUserRoleException(InvalidUserRoleException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Only Teacher can have the ClassHour");
	}
	
	@ExceptionHandler(UsersNotAssociatedWithAcademicProgram.class)
	public ResponseEntity<Object> userNotAssociatedWithAcademicProgram(UsersNotAssociatedWithAcademicProgram snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"No users associated with this AcademicProgram");
	}
	
	@ExceptionHandler(ClassRoomNotFreeException.class)
	public ResponseEntity<Object> classRoomNotFreeException(ClassRoomNotFreeException snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"Class Room Is Already assigned to another Subject");
	}
	
	@ExceptionHandler(InvalidClassHourDuratioion.class)
	public ResponseEntity<Object> invalidClassHourDurationException(InvalidClassHourDuratioion snf){
		return error(HttpStatus.NOT_FOUND,snf.getMessage() ,"ClassHourDurations and Schedule Durations are mismatch");
	}
	
	
	
}

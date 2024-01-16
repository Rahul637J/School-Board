package com.school.sba.exception;

public class DuplicateEntryException extends RuntimeException
{
	private String msg;
	
	 public DuplicateEntryException(String msg) 
	 {
		 this.msg=msg;
	}
	
	@Override
	public String getMessage() {
		return this.getMessage();
	}
	
	
	
	 

}

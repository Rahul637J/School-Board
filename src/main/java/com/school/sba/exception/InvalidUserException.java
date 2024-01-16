package com.school.sba.exception;

public class InvalidUserException extends RuntimeException
{
	private String msg;
	
	@Override
	public String getMessage() 
	{
		return this.msg;
	}

	public InvalidUserException(String msg) 
	{
		this.msg=msg;
	}
}

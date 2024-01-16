package com.school.sba.exception;

public class UserNotFoundException extends RuntimeException
{
	private String msg;
	
	public UserNotFoundException(String Msg) 
	{
		this.msg=msg;
	}
	@Override
	public String getMessage(){
		return this.msg;
	}
}

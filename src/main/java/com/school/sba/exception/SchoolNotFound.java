package com.school.sba.exception;

public class SchoolNotFound extends Exception
{
	private String msg;

	public SchoolNotFound(String msg) 
	{
		this.msg = msg;
	}
	
	@Override
	public String getMessage() 
	{
		return msg;
	}
}

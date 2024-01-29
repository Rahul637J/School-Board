package com.school.sba.requestdto;

import java.time.LocalTime;

import com.school.sba.enums.ClassStatus;

public class ClassHourRequest 
{
	private int classHourId;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;
}

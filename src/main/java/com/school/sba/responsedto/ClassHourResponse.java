package com.school.sba.responsedto;

import java.time.LocalTime;

import com.school.sba.enums.ClassStatus;

public class ClassHourResponse 
{
	private int classHourId;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;

}

package com.school.sba.requestdto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ClassHourRequest 
{
	private int classHourId;
	private int roomNo;
	private int usersId;
	private int subjectId;
}

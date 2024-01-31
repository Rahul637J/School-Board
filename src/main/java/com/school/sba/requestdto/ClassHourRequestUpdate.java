package com.school.sba.requestdto;

import com.school.sba.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassHourRequestUpdate 
{
	private int classHourId;
	private int roomNo;
	private ClassStatus classStatus;
	private int usersId;
	private int subjectId;
}

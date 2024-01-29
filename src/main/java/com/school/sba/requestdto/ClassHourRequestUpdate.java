package com.school.sba.requestdto;

import java.time.LocalTime;

import com.school.sba.entity.Subject;
import com.school.sba.entity.Users;
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
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;
	private Users users;
	private Subject subject;

}

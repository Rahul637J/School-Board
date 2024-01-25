package com.school.sba.requestdto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder

public class SubjectRequest 
{
//	@Column(unique = true)
	private List<String> subjectName; 

}

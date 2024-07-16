package com.school.sba.requestdto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Builder
public class ExcelRequestDto 
{
	private LocalDate fromDate;
	private LocalDate toDate;
	private String filePath;
}

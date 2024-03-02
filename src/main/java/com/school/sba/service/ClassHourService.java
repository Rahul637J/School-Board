package com.school.sba.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService
{
	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForAcademicProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(List<ClassHourRequest> classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateForAWeek(int academicProgramId);

	ResponseEntity<ResponseStructure<String>> getClassHourWithExcel(int programId, ExcelRequestDto excelRequestDto);

	ResponseEntity<?> writeToExcel(MultipartFile file, int programId, LocalDate fromDate,LocalDate toDate) throws IOException;
	
}

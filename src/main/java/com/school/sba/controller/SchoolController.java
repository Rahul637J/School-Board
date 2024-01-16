package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.school.sba.entity.School;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Controller
public class SchoolController 
{
	@Autowired
	
	
	private SchoolService schoolService;
	
	@PostMapping(value="/save")
	public  ResponseEntity<ResponseStructure<School>> saveSchool(@RequestBody School school)
	{
		return schoolService.saveScholl(school);
	}

}

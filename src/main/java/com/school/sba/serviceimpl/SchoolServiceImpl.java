//package com.school.sba.serviceimpl;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import com.school.sba.entity.School;
//import com.school.sba.exception.SchoolNotFound;
//import com.school.sba.repository.SchoolRepository;
//import com.school.sba.service.SchoolService;
//import com.school.sba.util.ResponseStructure;
//
//@Service
//public class SchoolServiceImpl implements SchoolService
//{
//	
//	@Autowired
//	private SchoolRepository repository; 
//	
//	@Autowired
//	private ResponseStructure<School> responseStructure;
//
//	@Override
//	public ResponseEntity<ResponseStructure<School>> saveScholl(School school) 
//	{
//		{
//			School sch = repository.save(school);
//			if ((sch != null)) {
//				responseStructure.setStatus(HttpStatus.FOUND.value());
//				responseStructure.setMsg("School Data Saved!!!!!!");
//				responseStructure.setData(sch);
//				return new ResponseEntity<ResponseStructure<School>>(responseStructure, HttpStatus.FOUND);
//			} else
//				return null;
//		}
//	}
//
//	@Override
//	public void getById(int schoolId) 
//	{
//		repository.getById(schoolId);
//		
//		try {
//			throw new SchoolNotFound("Invalid School Id");
//		} catch (SchoolNotFound e) {
//			e.getMessage();
//		}
//	}
//
//	@Override
//	public void updateById(int schoolId) {
//	}
//
//	@Override
//	public void deleteById(int schoolId) {
//		School deletebyId = repository.getById(schoolId);
//		repository.delete(deletebyId);
//	}
//
//	
//}

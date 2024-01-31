package com.school.sba.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.Users;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepo;

@Component
public class ScheduledJobs {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private AcademicProgramRepo academicProgramRepo;
	
	@Autowired
	private ClassHourRepo classHourRepo;
	
	@Autowired
	private SchoolRepository schoolRepository;

	@Scheduled(fixedDelay =1000l*60*5)
	public void testMethod()
		{
			System.out.println("Hello Schedule JObs");
			deleteUsers();
			deleteAcademicPrograms();
			deleteSchool();
		}
	
	private void deleteUsers()
	{
		List<Users> findAllByIsDelete = userRepo.findAllByDeleteUser(true);
		for(Users user:findAllByIsDelete)
		{
			System.out.println(user.getUserId());
			user.setAcademicProgramsList(null);
			userRepo.save(user);
			classHourRepo.findByUsers(user).forEach(classhour->{
				classhour.setUsers(null);
				classHourRepo.save(classhour);
			});			
			userRepo.delete(user);
		}
	}
	
	private void deleteAcademicPrograms()
	{
		List<AcademicProgram>academicProgram=academicProgramRepo.findAllByIsDelete(true);
		for(AcademicProgram ac: academicProgram)
		{
			System.out.println(ac.getProgramId());
			classHourRepo.findByAcademicProgram(ac).forEach(classhour->{
				classhour.setAcademicProgram(null);
				classHourRepo.save(classhour);
				academicProgramRepo.delete(ac);
			});	
		}
		
	}
	
	private void deleteSchool()
	{
		School school = schoolRepository.findByIsDelete(true);
		userRepo.findBySchool(school).forEach(user->{
			user.setSchool(null);
			userRepo.save(user);
		});
		academicProgramRepo.findBySchool(school).forEach(academicProgram->{
			academicProgram.setSchool(null);
			academicProgramRepo.save(academicProgram);
		});
		schoolRepository.delete(school);
	}
}

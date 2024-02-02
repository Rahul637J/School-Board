package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.serviceimpl.AcademicProgramServiceImpl;
import com.school.sba.serviceimpl.ClassHourServiceImpl;
import com.school.sba.serviceimpl.SchoolServiceImpl;
import com.school.sba.serviceimpl.UsersServiceImpl;

@Component
public class ScheduledJobs {
		
	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;
	
	@Autowired
	private UsersServiceImpl usersServiceImpl;
	
	@Autowired
	private SchoolServiceImpl schoolServiceImpl;
	
	@Autowired
	private ClassHourServiceImpl classHourServiceImpl;
	
	@Autowired 
	private AcademicProgramRepo academicProgramRepo;

	@Scheduled(fixedDelay =1000l*30)
	public void testMethod()
		{
			System.out.println("Hello Schedule JObs");
			usersServiceImpl.deleteUsers();
			academicProgramServiceImpl.deleteAcademicPrograms();
			schoolServiceImpl.deleteSchool();
		}
	
	@Scheduled(fixedDelay = 1000l*60*60)
	public void autoGenerateClassHour()
	{
		System.out.println("Entered");
		academicProgramRepo.findAll().forEach(academicprogram->{
			classHourServiceImpl.autoGenerateClassHourByScheduleJob(academicprogram.getProgramId());
		});
	}
}

package com.school.sba.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.ClassHourNotFound;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.exception.SubjectNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourRequestUpdate;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService
{
	@Autowired
	private AcademicProgramRepo academicProgramRepo;
	
	@Autowired
	private ClassHourRepo classHourRepo;
	
	@Autowired
	private ResponseStructure<String> responseStructure;
	
	private boolean isBreakTime(LocalDateTime beginsAt, LocalDateTime endsAt, Schedule schedule)
	{
		LocalTime breakTimeStart = schedule.getBreakTime();
		
		return ((breakTimeStart.isAfter(beginsAt.toLocalTime()) && breakTimeStart.isBefore(endsAt.toLocalTime())) || breakTimeStart.equals(beginsAt.toLocalTime()));
	}
	
	private boolean isLunchTime(LocalDateTime beginsAt, LocalDateTime endsAt , Schedule schedule)
	{
		LocalTime lunchTimeStart = schedule.getLunchTime();
		
		return ((lunchTimeStart.isAfter(beginsAt.toLocalTime()) && lunchTimeStart.isBefore(endsAt.toLocalTime())) || lunchTimeStart.equals(beginsAt.toLocalTime()));
    }
	
	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHourForAcademicProgram(int programId) 
	{
		return academicProgramRepo.findById(programId)
		.map(academicProgarm -> {
			School school = academicProgarm.getSchool();
			Schedule schedule = school.getSchedule();
			if(schedule!=null)
			{
				int classHourPerDay = schedule.getClassHoursPerDay();
				int classHourLength = (int) schedule.getClassHourLength().toMinutes();
				
				LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
				
				LocalDateTime lunchTimeStart = LocalDateTime.now().with(schedule.getLunchTime());
				LocalDateTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLength().toMinutes());
				LocalDateTime breakTimeStart = LocalDateTime.now().with(schedule.getBreakTime());
				LocalDateTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLength().toMinutes());
				
				for(int day = 1 ; day <= 6 ; day++)
				{
					for(int hour = 1 ; hour <= classHourPerDay+2 ; hour++)
					{
						ClassHour classHour = new ClassHour();
						LocalDateTime beginsAt = currentTime;
						LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);
						
						if(!isLunchTime(beginsAt, endsAt, schedule))
						{
							if(!isBreakTime(beginsAt, endsAt, schedule))
							{
								classHour.setBeginsAt(beginsAt);
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.NOT_SCHEDULE);
								
								currentTime = endsAt;
							}
							else
							{
								classHour.setBeginsAt(breakTimeStart);
								classHour.setEndsAt(breakTimeEnd);
								classHour.setClassStatus(ClassStatus.BREAK_TIME);
								currentTime = breakTimeEnd;
							}
						}
						else
						{
							classHour.setBeginsAt(lunchTimeStart);
							classHour.setEndsAt(lunchTimeEnd);
							classHour.setClassStatus(ClassStatus.LUNCH_TIME);
							currentTime = lunchTimeEnd;
						}
						classHour.setAcademicProgram(academicProgarm);
						classHourRepo.save(classHour);
					}
					currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
				}
	
			}
			else
				throw new ScheduleNotFoundException("The school does not contain any schedule, please provide a schedule to the school");
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMsg("ClassHour generated successfully for the academic progarm");
			responseStructure.setData("Class Hour generated for the current week successfully");
			return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.CREATED);
		})
		.orElseThrow(() -> new AcademicProgramNotFoundById("Invalid Program Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> updateClassHour(List<ClassHourRequestUpdate> classHourRequest) 
	{
		ArrayList<Object> arrayList=new ArrayList<Object>();
		classHourRequest.forEach(request->
		{
			if((request.getUsers().getUserId()!=0) && (request.getUsers().getUserRole()==UserRole.TEACHER))
			{
				if(request.getSubject().getSubjectId()!=0)
				{
					if(request.getClassHourId()!=0)
					{
						arrayList.add(request.getUsers().getUserId());
						arrayList.add(request.getSubject().getSubjectId());
						arrayList.add(request.getClassHourId());
					}
					else 
						throw new ClassHourNotFound("Invalid ClassHour ID");
				}
				else
					throw new SubjectNotFoundException("Invalid Subject Id");
			}
			else 
				throw new UsernameNotFoundException("Invalid User Id OR Only TEACHER can Have the classHour");
		});
		Integer hour = arrayList.get(2);
		if()
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMsg("ClassHour Upddated Successful!!!");
		responseStructure.setData("ClassHour Data Updated With RoomNO");
		return ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.CREATED);
	}	
}

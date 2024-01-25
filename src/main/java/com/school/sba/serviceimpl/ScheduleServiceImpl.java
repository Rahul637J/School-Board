package com.school.sba.serviceimpl;

import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.repository.ScheduleRepo;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleServiceImpl implements ScheduleService
{
	@Autowired
	private ResponseStructure<ScheduleResponse> responseStructure;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private ScheduleRepo scheduleRepo;
	
	private Schedule mapToSchedule(ScheduleRequest request) 
	{
		return Schedule.builder()
				.opensAt(request.getOpensAt())
				.closesAt(request.getClosesAt())
				.classHoursPerDay(request.getClassHoursPerDay())
				.classHourLength(Duration.ofMinutes(request.getClassHourLengthInMinutes()))
				.breakTime(request.getBreakTime())
				.breakLength(Duration.ofMinutes(request.getBreakLengthInMinutes()))
				.lunchTime(request.getLunchTime())
				.lunchLength(Duration.ofMinutes(request.getBreakLengthInMinutes()))
				.build();
	}
	
	private ScheduleResponse mapToResponse(Schedule schedule) 
	{
		return ScheduleResponse.builder()
				.scheduleId(schedule.getScheduleId())
				.opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt())
				.classHoursPerDay(schedule.getClassHoursPerDay())
				.classHourLength((int)schedule.getClassHourLength().toMinutesPart())
				.breakTime(schedule.getBreakTime())
				.breakLength((int)schedule.getBreakLength().toMinutesPart())
				.lunchTime(schedule.getLunchTime())
				.lunchLength((int)schedule.getLunchLength().toMinutesPart())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(int schoolId, ScheduleRequest request) 
	{
			School school = schoolRepository.findById(schoolId).orElseThrow(()-> new SchoolNotFound("School Is not present"));
			if(school.getSchedule()==null) {
			Schedule schedule = scheduleRepo.save(mapToSchedule(request));
			school.setSchedule(schedule);
			school=schoolRepository.save(school);
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMsg("Schedule Saved Successfully");
			responseStructure.setData(mapToResponse(schedule));
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.CREATED);
		}
		else {
			throw new DuplicateEntryException("Schedule Already Exist");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) 
	{
		School school = schoolRepository.findById(schoolId).orElseThrow(()-> new SchoolNotFound("Invalid School Id"));
		if(school.getSchedule()!=null)
		{
			Schedule schedule = school.getSchedule();
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMsg("Schedule found successfull!!");
			responseStructure.setData(mapToResponse(schedule));
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.FOUND);
		}
		else {
			throw new ScheduleNotFoundException("Schedule not found");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId, ScheduleRequest request) {
		Schedule schedule=scheduleRepo.findById(scheduleId).orElseThrow(()-> new ScheduleNotFoundException("Can't find any schedule in the given ID"));
	    if (Objects.nonNull(request.getOpensAt())) {
	        schedule.setOpensAt(request.getOpensAt());
	    }
	    if (Objects.nonNull(request.getClosesAt())) {
	        schedule.setClosesAt(request.getClosesAt());
	    }
	    if (Objects.nonNull(request.getBreakTime())) {
	        schedule.setBreakTime(request.getBreakTime());
	    }
	    if (Objects.nonNull(request.getBreakLengthInMinutes())) {
	        schedule.setBreakLength(Duration.ofMinutes(request.getBreakLengthInMinutes()));
	    }
	    if (Objects.nonNull(request.getClassHoursPerDay())) {
	        schedule.setClassHoursPerDay(request.getClassHoursPerDay());
	    }
	    if (Objects.nonNull(request.getClassHourLengthInMinutes())) {
	        schedule.setClassHourLength(Duration.ofMinutes(request.getClassHourLengthInMinutes()));
	    } else {
	        schedule.setClassHourLength(null);
	    }
	    if (Objects.nonNull(request.getLunchTime())) {
	        schedule.setLunchTime(request.getLunchTime());
	    }
	    if (Objects.nonNull(request.getLunchLengthInMinutes())) {
	        schedule.setLunchLength(Duration.ofMinutes(request.getLunchLengthInMinutes()));
	    } else {
	        schedule.setLunchLength(null);
	    }
		scheduleRepo.save(schedule);
		responseStructure.setStatus(HttpStatus.ACCEPTED.value());
		responseStructure.setMsg("Schedule updated");
		responseStructure.setData(mapToResponse(schedule));
		return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.ACCEPTED);
	}


	


	
	

}

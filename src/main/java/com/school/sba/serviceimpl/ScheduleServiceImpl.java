package com.school.sba.serviceimpl;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exception.InvalidClassHourDuratioion;
import com.school.sba.exception.InvalidClassHourIdException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.repository.ScheduleRepo;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleServiceImpl implements ScheduleService {
	
	@Autowired
	private ResponseStructure<ScheduleResponse> responseStructure;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private ScheduleRepo scheduleRepo;

	private Schedule mapToSchedule(ScheduleRequest request) {
		return Schedule.builder().opensAt(request.getOpensAt()).closesAt(request.getClosesAt())
				.classHoursPerDay(request.getClassHoursPerDay())
				.classHourLength(Duration.ofMinutes(request.getClassHourLengthInMinutes()))
				.breakTime(request.getBreakTime()).breakLength(Duration.ofMinutes(request.getBreakLengthInMinutes()))
				.lunchTime(request.getLunchTime()).lunchLength(Duration.ofMinutes(request.getBreakLengthInMinutes()))
				.build();
	}

	private ScheduleResponse mapToResponse(Schedule schedule) {
		return ScheduleResponse.builder().scheduleId(schedule.getScheduleId()).opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt()).classHoursPerDay(schedule.getClassHoursPerDay())
				.classHourLength((int) schedule.getClassHourLength().toMinutesPart()).breakTime(schedule.getBreakTime())
				.breakLength((int) schedule.getBreakLength().toMinutesPart()).lunchTime(schedule.getLunchTime())
				.lunchLength((int) schedule.getLunchLength().toMinutesPart()).build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(int schoolId, ScheduleRequest request) 
	{
		School school = schoolRepository.findById(schoolId)
				.orElseThrow(() -> new SchoolNotFound("School Is not present"));
		if (school.getSchedule() == null) 
		{
			LocalTime opensAt = request.getOpensAt();
			LocalTime closesAt = request.getClosesAt();
			
	        // Calculate duration between beginsAt and endsAt
	        Duration duration = Duration.between(opensAt, closesAt);

	        // Convert duration to seconds
	        long seconds = duration.getSeconds();

	        // Convert duration to double (using seconds as a double)
	        double durationOfDay = (double) seconds;
			
	        if(durationOfDay==((request.getClassHoursPerDay()*request.getClassHourLengthInMinutes())+request.getBreakLengthInMinutes()+request.getLunchLengthInMinutes()))
	        {
	        	int minutes =0;
	        	LocalTime start = request.getOpensAt();
	        	LocalTime breakTime = request.getBreakTime();
	        	while(start==request.getClosesAt())
	        	{
	        		minutes+=request.getClassHourLengthInMinutes();
	        		start=start.plusMinutes(request.getClassHourLengthInMinutes());
	        		if((request.getOpensAt().plusMinutes(minutes)==request.getBreakTime()||request.getOpensAt().plusMinutes(minutes).isBefore(request.getBreakTime()))&&
	        				(request.getOpensAt().plusMinutes(minutes)==request.getLunchTime()||request.getOpensAt().plusMinutes(minutes).isBefore(request.getLunchTime())))
	        			continue;
	        		else 
	        			throw new InvalidClassHourDuratioion("Irrevelant BreakHour Time OR Irrevelant LunchHour Time");
	        	}
	        	
				Schedule schedule = scheduleRepo.save(mapToSchedule(request));
				school.setSchedule(schedule);
				school = schoolRepository.save(school);
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMsg("Schedule Saved Successfully");
				responseStructure.setData(mapToResponse(schedule));
				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.CREATED);
		     }
	        else
	        	throw new InvalidClassHourDuratioion("Irrevelant ClassHour and Duration of the Day");
		}
		 else 
	        {
	        	throw new InvalidClassHourIdException("Schedule Already Exist");
	        }
	}		

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) {
		School school = schoolRepository.findById(schoolId).orElseThrow(() -> new SchoolNotFound("Invalid School Id"));
		if (school.getSchedule() != null) {
			Schedule schedule = school.getSchedule();
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMsg("Schedule found successfull!!");
			responseStructure.setData(mapToResponse(schedule));
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.FOUND);
		} else {
			throw new ScheduleNotFoundException("Schedule not found");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId, ScheduleRequest request) 
	{

		return scheduleRepo.findById(scheduleId).map(schedule->
		{
			LocalTime opensAt = request.getOpensAt();
			LocalTime closesAt = request.getClosesAt();
			
	        // Calculate duration between beginsAt and endsAt
	        Duration duration = Duration.between(opensAt, closesAt);

	        // Convert duration to seconds
	        long seconds = duration.getSeconds();

	        // Convert duration to double (using seconds as a double)
	        double durationOfDay = (double) seconds;
			
	        if(durationOfDay==((request.getClassHoursPerDay()*request.getClassHourLengthInMinutes())+request.getBreakLengthInMinutes()+request.getLunchLengthInMinutes()))
	        {
	        	int minutes =0;
	        	LocalTime start = request.getOpensAt();
	        	LocalTime breakTime = request.getBreakTime();
	        	while(start==request.getClosesAt())
	        	{
	        		minutes+=request.getClassHourLengthInMinutes();
	        		start=start.plusMinutes(request.getClassHourLengthInMinutes());
	        		if((request.getOpensAt().plusMinutes(minutes)==request.getBreakTime()||request.getOpensAt().plusMinutes(minutes).isBefore(request.getBreakTime()))&&
	        				(request.getOpensAt().plusMinutes(minutes)==request.getLunchTime()||request.getOpensAt().plusMinutes(minutes).isBefore(request.getLunchTime())))
	        			continue;
	        		else 
	        			throw new InvalidClassHourDuratioion("Irrevelant BreakHour Time OR Irrevelant LunchHour Time");
	        	}
	        	
			Schedule schedule1=mapToSchedule(request);
			schedule.setBreakLength(schedule1.getBreakLength());
			schedule.setBreakTime(schedule1.getBreakTime());
			schedule.setClassHourLength(schedule1.getClassHourLength());
			schedule.setClassHoursPerDay(schedule1.getClassHoursPerDay());
			schedule.setOpensAt(schedule1.getOpensAt());
			schedule.setClosesAt(schedule1.getClosesAt());
			schedule.setLunchLength(schedule1.getLunchLength());
			schedule.setLunchTime(schedule1.getLunchTime());
	        }
	        else
	        	throw new InvalidClassHourDuratioion("Irrevelant ClassHour and Duration of the Day");
			responseStructure.setStatus(HttpStatus.ACCEPTED.value());
			responseStructure.setMsg("Schedule updated");
			responseStructure.setData(mapToResponse(schedule));
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.ACCEPTED);
		}).orElseThrow(()-> new ScheduleNotFoundException("Invalid Schedule ID"));
		
	}

	private ResponseEntity<ResponseStructure<List<ScheduleResponse>>> deleteSchedule(List<Schedule> schedule) {
		ArrayList<ScheduleResponse> alist = new ArrayList<>();
		schedule.forEach(schedule1 -> {
			scheduleRepo.delete(schedule1);
			alist.add(mapToResponse(schedule1));
		});
		ResponseStructure<List<ScheduleResponse>> structure = new ResponseStructure<>();
		structure.setData(alist);
		structure.setMsg("DELETED Successfully ");
		structure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<List<ScheduleResponse>>>(structure, HttpStatus.OK);
	}

}

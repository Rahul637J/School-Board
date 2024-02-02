package com.school.sba.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.ClassRoomNotFreeException;
import com.school.sba.exception.InvalidClassHourIdException;
import com.school.sba.exception.InvalidUserRoleException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SubjectNotFoundException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.ClassHourRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
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
	
	@Autowired
	private ResponseStructure<List<ClassHourResponse>> responseStructure1;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private SubjectRepo subjectRepo;
	
	public ClassHour mapToClassHour(ClassHour classHour) {
		return ClassHour.builder().academicProgram(classHour.getAcademicProgram())
				.beginsAt(classHour.getBeginsAt().plusWeeks(1)).classStatus(classHour.getClassStatus())
				.endsAt(classHour.getEndsAt().plusWeeks(1)).roomNo(classHour.getRoomNo())
				.subject(classHour.getSubject()).users(classHour.getUsers()).build();
	}
	
	private ClassHourResponse mapToClassHourResponse(ClassHour save) {
		return ClassHourResponse.builder()
				.classHourId(save.getClassHourId())
				.beginsAt(save.getBeginsAt())
				.roomNo(save.getRoomNo())
				.subject(save.getSubject())
				.build();
	}
	
	String leave="SUNDAY";
	
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
				LocalDateTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLength().toMinutes());//here duration type converting into localdateandtime
				LocalDateTime breakTimeStart = LocalDateTime.now().with(schedule.getBreakTime());
				LocalDateTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLength().toMinutes());
				
				int days= 7-currentTime.getDayOfWeek().getValue();
				
				for(int day = 1 ; day <= 6+days ; day++)
				{
					if(currentTime.getDayOfWeek().equals(leave))
					currentTime.plusDays(1);
					for(int hour = 1 ; hour <= classHourPerDay+2 ; hour++)//here class hours per day with lunch hour and break hour
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
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(List<ClassHourRequest> classHourRequest) 
	{
		
		ArrayList<ClassHourResponse> alist = new ArrayList<>();
		if (!classHourRequest.isEmpty()) {
			classHourRequest.forEach(classHour -> {
				classHourRepo.findById(classHour.getClassHourId()).map(classHourdb -> 
				{
					if (!classHourRepo.existsByBeginsAtAndRoomNo(classHourdb.getBeginsAt(),classHour.getRoomNo())) 
					{
						subjectRepo.findById(classHour.getSubjectId()).map(subject -> 
						{
							if (LocalDateTime.now().isBefore(classHourdb.getEndsAt())
									&& LocalDateTime.now().isAfter(classHourdb.getBeginsAt())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.ONGOING);
							} else if (classHourdb.getBeginsAt().toLocalTime().equals(userRepo
									.findById(classHour.getUsersId()).get().getSchool().getSchedule().getLunchTime())) {
								classHourdb.setClassStatus(ClassStatus.LUNCH_TIME);
								classHourdb.setSubject(null);

							} else if (classHourdb.getBeginsAt().toLocalTime().equals(userRepo
									.findById(classHour.getUsersId()).get().getSchool().getSchedule().getBreakTime())) {
								classHourdb.setClassStatus(ClassStatus.BREAK_TIME);
								classHourdb.setSubject(null);

							} else if (classHourdb.getBeginsAt().isBefore(LocalDateTime.now())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.COMPLETE);
								
							} else if (classHourdb.getBeginsAt().isAfter(LocalDateTime.now())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.UPCOMING);
							}
							
							return classHourdb;
							
						}).orElseThrow(() -> new SubjectNotFoundException("invalid Subject ID  !!!"));
					} else {
						throw new ClassRoomNotFreeException(" not free !!!");
					}

					userRepo.findById(classHour.getUsersId()).map(user -> {

						if (user.getUserRole().toString().equals("TEACHER")) {

							classHourdb.setUsers(user);
							return classHourdb;
						} else {
							throw new InvalidUserRoleException("invalid User role !!!TEACHER required ");
						}
					}).orElseThrow(() -> new UserNotFoundException("invalid ID!!!"));
					alist.add(mapToClassHourResponse(classHourRepo.save(classHourdb)));
					responseStructure1.setData(alist);
					responseStructure1.setMsg("updated successuflly ");
					responseStructure1.setStatus(HttpStatus.ACCEPTED.value());
					return classHourdb;
				}).orElseThrow(() -> new InvalidClassHourIdException("invalid ID"));
			});
		   }
			return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(responseStructure1, HttpStatus.OK);
		 }
	
	private ResponseEntity<ResponseStructure<List<ClassHourResponse>>> deleteClassHour(List<ClassHour> classHourList)
	{
		ArrayList<ClassHourResponse> responseList=new ArrayList<ClassHourResponse>();
		classHourList.forEach(classHour->{
			classHourRepo.delete(classHour);
			responseList.add(mapToClassHourResponse(classHour));
		});
		ResponseStructure<List<ClassHourResponse>> responseStructure=new ResponseStructure<List<ClassHourResponse>>();
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMsg("ClassHour Deleted Successful!!!");
		responseStructure.setData(responseList);
		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(responseStructure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateForAWeek(int academicProgramId) 
	{
		academicProgramRepo.findById(academicProgramId).map(program -> {
			LocalDateTime endsALocalTime = program.getClassHour().getLast().getEndsAt();
		
			LocalDateTime minusDays = endsALocalTime.minusDays(5).minusHours(8).minusMinutes(30);
			LocalDateTime minusDays2 = endsALocalTime;
			System.out.println(minusDays + " " + minusDays2);
		
			List<ClassHour> findByEndsALocalTimeBetween = classHourRepo.findByEndsAtBetween(minusDays,minusDays2);
		
			ArrayList<ClassHourResponse> alist = new ArrayList<>();
		
			findByEndsALocalTimeBetween.forEach(find -> {
				alist.add(mapToClassHourResponse(classHourRepo.save(mapToClassHour(find))));
			});
			responseStructure1.setData(alist);
			responseStructure1.setMsg("next week data classhour !!!");
			responseStructure1.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(responseStructure1,
					HttpStatus.CREATED);
		});
		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(responseStructure1, HttpStatus.CREATED);
	}	
	
	public void autoGenerateClassHourByScheduleJob(int programId)
	{
		generateClassHourForAcademicProgram(programId);
	}
}	


package com.school.sba.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.ClassRoomNotFreeException;
import com.school.sba.exception.DuplicateEntryException;
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
import com.school.sba.requestdto.ExcelRequestDto;
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
	
	public boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLength().toMinutes());
		return currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd);
	}

	public boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {

		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLength().toMinutes());

		return currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForAcademicProgram(int programId) 
	{

		return academicProgramRepo.findById(programId).map(program -> {
			School school = program.getSchool();
			if (school == null)
				throw new ScheduleNotFoundException("school not yet added ");
			Schedule schedule = school.getSchedule();
			List<ClassHourResponse> responses = new ArrayList<>();

			List<ClassHour> classHours = new ArrayList<>();
			if (program.getClassHour().isEmpty()) {
				if (schedule != null) {
					long classHoursInMinutes = schedule.getClassHourLength().toMinutes();
					int classHoursPerDay = schedule.getClassHoursPerDay();

					LocalTime closesAt = schedule.getClosesAt();

					LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());

					LocalTime lunchTimeStart = schedule.getLunchTime();
					LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLength().toMinutes());

					LocalTime breakTimestart = schedule.getBreakTime();

					LocalTime breakTimeEnd = breakTimestart.plusMinutes(schedule.getBreakLength().toMinutes());

					int days = 7 - currentTime.getDayOfWeek().getValue();

					System.out.println(days + " is the number of days ");

					for (int day = 1; day <= 7 + days; day++) {
						System.out.println(currentTime.getDayOfWeek().name() + " is the day of the week");

						if (!currentTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
							for (int hour = 0; hour <= classHoursPerDay + 2; hour++) {
								ClassHour classHour = new ClassHour();
								if (currentTime.toLocalTime().isBefore(closesAt)
										&& !currentTime.toLocalTime().equals(closesAt)) {
									LocalDateTime beginsAt = currentTime;
									LocalDateTime endsAt = currentTime.plusMinutes(classHoursInMinutes);

									if (!currentTime.toLocalTime().equals(lunchTimeStart)
											&& !isLunchTime(currentTime, schedule)) {
										if (!currentTime.toLocalTime().equals(breakTimestart)
												&& !isBreakTime(currentTime, schedule)) {
											classHour.setBeginsAt(beginsAt);
											classHour.setEndsAt(endsAt);
											classHour.setClassStatus(ClassStatus.NOT_SCHEDULE);

											currentTime = endsAt;

										} else {
											classHour.setBeginsAt(
													breakTimestart.atDate(currentTime.toLocalDate()));
											classHour.setEndsAt(breakTimeEnd.atDate(currentTime.toLocalDate()));
											currentTime = breakTimeEnd.atDate(currentTime.toLocalDate());

											classHour.setClassStatus(ClassStatus.BREAK_TIME);

										}
									} else {
										classHour
												.setBeginsAt(lunchTimeStart.atDate(currentTime.toLocalDate()));
										classHour.setEndsAt(lunchTimeEnd.atDate(currentTime.toLocalDate()));
										currentTime = lunchTimeEnd.atDate(currentTime.toLocalDate());

										classHour.setClassStatus(ClassStatus.LUNCH_TIME);
									}
									classHour.setAcademicProgram(program);

									ClassHour savedClassHour = classHourRepo.save(classHour);

									classHours.add(savedClassHour);

									responses.add(mapToClassHourResponse(savedClassHour));

								}
							}
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());

						} else {
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
						}

					}
				} else {
					throw new ScheduleNotFoundException("schedule not found !!!");
				}
			} else {
				throw new DuplicateEntryException(" class hours already present !!!");
			}

			program.setClassHour(classHours);
			academicProgramRepo.save(program);
			responseStructure1.setData(responses);
			responseStructure1.setMsg("Added Successfully !!!");
			responseStructure1.setStatus(HttpStatus.OK.value());

			return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(responseStructure1, HttpStatus.OK);
		}).orElseThrow(()-> new ScheduleNotFoundException("Schedule Not Found!!!!"));
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
		
			LocalDateTime minusDays = program.getClassHour().getLast().getBeginsAt();
			LocalDateTime minusDays2 = program.getClassHour().getLast().getEndsAt();
		
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

	@Override
	public ResponseEntity<ResponseStructure<String>> getClassHourWithExcel(int programId,ExcelRequestDto excelRequestDto) 
	{
		LocalDateTime from = excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
		LocalDateTime to = excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT);
		
		 Random random = new Random();

	        int randomNumber = random.nextInt(10);
		String filePath = excelRequestDto.getFilePath()+"\\test"+randomNumber+".xlsx";
		
		academicProgramRepo.findById(programId).map(program->{
		List<ClassHour> classHour = classHourRepo.findAllByAcademicProgramAndBeginsAtBetween(program,from,to);
		
		XSSFWorkbook workBook=new XSSFWorkbook();
		Sheet sheet=workBook.createSheet();
		
		int rowNumber=0;
		Row header=sheet.createRow(rowNumber);
		header.createCell(0).setCellValue("Date");
		header.createCell(1).setCellValue("Begins At");
		header.createCell(2).setCellValue("Ends At");
		header.createCell(3).setCellValue("Subject");
		header.createCell(4).setCellValue("Teacher");
		header.createCell(5).setCellValue("Room No");
		
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		for(ClassHour classHour2:classHour)
		{
			Row row=sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue(dateFormatter.format(classHour2.getBeginsAt()));
			row.createCell(1).setCellValue(timeFormatter.format(classHour2.getBeginsAt()));
			row.createCell(2).setCellValue(timeFormatter.format(classHour2.getEndsAt()));
			row.createCell(3).setCellValue(classHour2.getRoomNo());
			if(classHour2.getSubject()==null)
				row.createCell(4).setCellValue("");
			else
				row.createCell(4).setCellValue(classHour2.getSubject().getSubjectName());	
			if(classHour2.getUsers()==null)
				row.createCell(5).setCellValue("");
			else
				row.createCell(5).setCellValue(classHour2.getUsers().getUserName());	
		}
		
		try {
			workBook.write(new FileOutputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Success";
		}).orElseThrow(()-> new AcademicProgramNotFoundById("Invalid Academic Program Id"));
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMsg("Data Fetched Successful");
		responseStructure.setData("Success");
		return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.OK);
	}


	@Override
	public ResponseEntity<?> writeToExcel(MultipartFile file,int programId, LocalDate fromDate, LocalDate toDate) throws IOException {
		
		AcademicProgram program = academicProgramRepo.findById(programId).orElseThrow(()-> new AcademicProgramNotFoundById("The Given ID Is Not Present In The Database"));
		LocalDateTime from = fromDate.atTime(LocalTime.MIDNIGHT);
		LocalDateTime to = toDate.atTime(LocalTime.MIDNIGHT).plusDays(1);
		
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
			List<ClassHour> clist = classHourRepo.findAllByAcademicProgramAndBeginsAtBetween(program,from,to);
			
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			
			workbook.forEach(sheet ->{
				int rowNumber = 0;
				Row header = sheet.createRow(rowNumber);
				header.createCell(0).setCellValue("Date");
				header.createCell(1).setCellValue("Begin Time");
				header.createCell(2).setCellValue("End Time");
				header.createCell(3).setCellValue("Subject");
				header.createCell(4).setCellValue("Teacher");
				header.createCell(5).setCellValue("Room No");
				
				for (ClassHour classhour :clist ) {
					Row row = sheet.createRow(++rowNumber);
					row.createCell(0).setCellValue(dateFormatter.format(classhour.getBeginsAt()));
					row.createCell(1).setCellValue(timeFormatter.format(classhour.getBeginsAt()));
					row.createCell(2).setCellValue(timeFormatter.format(classhour.getEndsAt()));
					if(classhour.getSubject() == null) {
						row.createCell(3).setCellValue("");
					}else {
						row.createCell(3).setCellValue(classhour.getSubject().getSubjectName());
					}
					if(classhour.getUsers() == null) {
						row.createCell(4).setCellValue("");
					}else {
						row.createCell(4).setCellValue(classhour.getUsers().getUserName());
					}
					if(classhour.getRoomNo() == 0) {
						row.createCell(5).setCellValue("");
					}else {
						row.createCell(5).setCellValue(classhour.getRoomNo());
					}					
				}
				
			});
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			workbook.close();
			
			byte [] byteData = outputStream.toByteArray();
			
			return ResponseEntity.ok()
					.header("Content Disposition", "attachment; filename="+file.getOriginalFilename())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteData);
	}
}	


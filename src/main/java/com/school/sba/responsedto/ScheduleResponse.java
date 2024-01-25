package com.school.sba.responsedto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ScheduleResponse 
{
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int       classHoursPerDay;
	private int classHourLength;
	private LocalTime breakTime;
	private int breakLength;
	private LocalTime lunchTime;
	private int lunchLength;

}

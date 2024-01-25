package com.school.sba.requestdto;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScheduleRequest 
{
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int       classHoursPerDay;
	private int       classHourLengthInMinutes;
	private LocalTime breakTime;
	private int       breakLengthInMinutes;
	private LocalTime lunchTime;
	private int  lunchLengthInMinutes;
	@Override
	public String toString() {
		return "ScheduleRequest [opensAt=" + opensAt + ", closesAt=" + closesAt + ", classHoursPerDay="
				+ classHoursPerDay + ", classHourLengthInMinutes=" + classHourLengthInMinutes + ", breakTime="
				+ breakTime + ", breakLengthInMinutes=" + breakLengthInMinutes + ", lunchTime=" + lunchTime
				+ ", lunchLengthInMinutes=" + lunchLengthInMinutes + "]";
	}
	
	
	
}

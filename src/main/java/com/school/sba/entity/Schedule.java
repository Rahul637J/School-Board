package com.school.sba.entity;

import java.time.LocalTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Schedule 
{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int       scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int       classHoursPerDay;
	private LocalTime classHourLength;
	private LocalTime breakTime;
	private LocalTime breakLength;
	private LocalTime lunchTime;
	private LocalTime lunchLength;

}

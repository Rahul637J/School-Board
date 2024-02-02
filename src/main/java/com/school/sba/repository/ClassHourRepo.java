package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Users;

public interface ClassHourRepo extends JpaRepository<ClassHour, Integer>
{

	boolean existsByBeginsAtAndRoomNo(LocalDateTime beginsAt, int roomNo);

	List<ClassHour> findByUsers(Users user);

	List<ClassHour> findByAcademicProgram(AcademicProgram ac);

	List<ClassHour> findByEndsAtBetween(LocalDateTime minusDays, LocalDateTime minusDays2);


}

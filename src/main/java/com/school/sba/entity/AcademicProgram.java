package com.school.sba.entity;

import java.time.LocalTime;
import java.util.List;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcademicProgram 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int programId;
	private ProgramType programType;
	private String programName;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private boolean isDelete;
	
	@ManyToOne
	@JoinColumn(name="schoolId")
	private School school;
	
	@ManyToMany
	private List<Subject> subject;
	
	@ManyToMany
	private List<Users> usersList;
	
	@OneToMany(mappedBy = "academicProgram")
	private List<ClassHour> classHour;
}

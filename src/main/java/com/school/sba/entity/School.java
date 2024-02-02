package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School 
{
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int schoolId;
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;
	private boolean isDelete;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "scheduleId")
	private Schedule schedule;
	
	@OneToMany(mappedBy = "school")
	private List<AcademicProgram> academicProgramsList;
	
	
}

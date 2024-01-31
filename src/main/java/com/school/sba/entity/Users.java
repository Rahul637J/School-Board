package com.school.sba.entity;

import java.util.List;

import com.school.sba.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Users 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@Column(unique = true)
	private String userName;
	private String password;
	private String firstName;	
	private String lastName;
	@Column(unique = true)
	private long contactNo;
	@Column(unique = true)
	private String email;
	private UserRole userRole;
	private boolean deleteUser;
	
	@ManyToOne
	private School school;
	
	@ManyToMany(mappedBy = "usersList")
	private List<AcademicProgram> academicProgramsList;
	
	@ManyToOne
	private Subject subject;
	
}

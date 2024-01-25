package com.school.sba.responsedto;

import org.springframework.stereotype.Component;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Builder
public class SchoolResponse {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int schoolId;
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;
}

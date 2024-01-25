package com.school.sba.requestdto;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
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
public class SchoolRequest 
{
//	@NotBlank(message = "School Name Cannot be Blank")
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;

}

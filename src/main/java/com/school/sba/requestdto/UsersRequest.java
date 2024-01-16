package com.school.sba.requestdto;


import com.school.sba.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsersRequest 
{
	@NotBlank(message = "USERNAME IS MANDATORY")
	private String userName;
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must have 1 uppercase, 1 lowercase, 1 number, 1 special character, and be at least 8 characters long")
	private String userPassword;

	@NotBlank(message = "FirstName is mandatory")
	private String userFirstName;
	private String userLastName;
//	@Size(min = 10,max = 10)
	private long userContactNo;
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	@NotBlank(message = "email canot be blank")
	private String userEmail;
	@NotNull(message = "User Role is mandatory")
	private UserRole userRole;

}

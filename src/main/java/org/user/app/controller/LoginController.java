package org.user.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;





@Controller
@Tag(name = "Users", description = "Operations related to general use")	
public class LoginController {
	@GetMapping("/login")
	@Operation(summary = "Display login page", 
	           description = "Render the login page for users to enter their credentials")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Login page successfully rendered")
	})
	public String login() {
	    return "login"; 
	}
}

	
	
	
	
	

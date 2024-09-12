package org.user.app.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



import io.swagger.v3.oas.annotations.tags.Tag;

	

@Controller
@Tag(name = "Users", description = "Operations related to general use")
	
public class LoginController {
 
	    
	@GetMapping("/login")
	    public String login() {
	        return "login"; // This returns the login page view.
	    }
	
    
}

	
	
	
	
	

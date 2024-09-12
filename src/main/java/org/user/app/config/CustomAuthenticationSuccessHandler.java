package org.user.app.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
	                                    Authentication authentication) throws IOException, ServletException {
	    // Get the role of the logged-in user
	    String role = authentication.getAuthorities().iterator().next().getAuthority();
	    
	    // Redirect based on the role
	    if (role.equals("ROLE_DOCTOR")) {
	        response.sendRedirect("/doctors/view");
	    } else if (role.equals("ROLE_PATIENT")) {
	        response.sendRedirect("/patients/view");
	    } else if (role.equals("ROLE_ADMIN")) {
	        response.sendRedirect("/admin/home");
	    } else {
	        response.sendRedirect("/error"); // Default if no specific role
	    }
	}
}
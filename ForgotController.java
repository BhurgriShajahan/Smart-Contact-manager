package com.spring.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.dao.UserRepository;
import com.spring.entities.User;
import com.spring.service.EmailService;

import jakarta.servlet.http.HttpSession;

@org.springframework.stereotype.Controller
public class ForgotController {

	Random random = new Random(1000);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email ,HttpSession session) {
		
		int otp = random.nextInt(999999);
		
		String subject  ="OTP From SCM";
//		String message = ""
//					+"<div style='border:1px solid #e2e2e2; padding:20px;'>"
//					+"<h1>"
//					+"OTP is "
//					+"<b>"+otp
//					+"</b>"
//					+"</h1>"
//					+"</div>";
		String message = ""+otp;
		String to = email;
		
		
		boolean flag = emailService.sendEmail(subject , message , to);
		
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verity_otp";
		}
		else {
			session.setAttribute("message", "Check your email id !!");
			return "forgot_email_form";
		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp , HttpSession session) {
		
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		
		if(myOtp == otp) {
			User user = userRepository.findByEmail(email);
			if(user == null) {
				session.setAttribute("message", "User does not exits with this email !!");
				return "forgot_email_form";
			}
			else {
				
			}
			
			return "password_change_form";
		}
		else {
			session.setAttribute("message", "You have entered wrong otp");
			return "verity_otp";
		}
	}
	
	
}

package com.spring.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dao.UserRepository;
import com.spring.entities.User;
import com.spring.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title" , "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title" , "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title" , "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login - Smart Contact Manger");
		model.addAttribute("user",new User());
		return "login";
	}
	
	/*
	 * @ResponseBody
	 * 
	 * @PostMapping("/dologin") public String login() { return "login"; }
	 */

	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user") User user , BindingResult bindingResult , @RequestParam(value= "agreement",defaultValue = "false") boolean agreement , Model model , HttpSession session) {

		try {
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}

			if(bindingResult.hasErrors()) {
				System.out.println("ERROR " + bindingResult.toString());
				model.addAttribute("user",user);
				return "signup";
			}

//			user.setRole("ROLE_ADMIN");
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));


			System.out.println("Agreement" + agreement);
			System.out.println("User" +user);

			User result = this.userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Rigistered !!" , "alert-success"));
			return "signup";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" +e.getMessage() , "alert-danger"));
			return "signup";
		}

	}
	

	@PostMapping("/add_user")
	public String addUserAdmin(@Valid @ModelAttribute("user") User user , BindingResult bindingResult , @RequestParam(value= "agreement",defaultValue = "false") boolean agreement , Model model , HttpSession session) {

		try {
			if(!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}

			if(bindingResult.hasErrors()) {
				System.out.println("ERROR " + bindingResult.toString());
				model.addAttribute("user",user);
				return "/admin/signup";
			}

//			user.setRole("ROLE_ADMIN");
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			String password = user.getPassword();
			System.out.println(password);
		
			user.setPassword(passwordEncoder.encode(password));
			

			System.out.println("Agreement" + agreement);
			System.out.println("User" +user);

			User result = this.userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Rigistered !!" , "alert-success"));
			return "admin/signup";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" +e.getMessage() , "alert-danger"));
			return "admin/signup";
		}

	}
	
	
	
	
}

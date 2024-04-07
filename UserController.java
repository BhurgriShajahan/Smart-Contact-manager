package com.spring.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dao.ContactRepository;
import com.spring.dao.UserRepository;
import com.spring.entities.Contact;
import com.spring.entities.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@org.springframework.stereotype.Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addCommomData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("USERNAME " + userName);
		User user = userRepository.findByEmail(userName);
		System.out.println("User " + user.toString());
		model.addAttribute(user);

	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User_Dashbord - Smart Contact Manger");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String oneAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact,  BindingResult result, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session , Model model) {
		System.out.println("this is conact hander");
		try {
			
			if(result.hasErrors()) {
				System.out.println("ERRORS "+result.toString());
				model.addAttribute("contact",contact);
				return "normal/add_contact_form";
			}

			System.out.println("Contact Data " + contact);
			String userName = principal.getName();
			User user = userRepository.findByEmail(userName);
			

			if (file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("registration1.png");
			} else {

				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContact().add(contact);
			userRepository.save(user);
			session.setAttribute("message",
			new com.spring.helper.Message("Your Contact is add !! Add more..", "success"));
		} catch (Exception e) {
			System.out.println("ERORR " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message",new com.spring.helper.Message("Something went wrong !! Try again..", "danger"));
			return "normal/add_contact_form";
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/view_contact/{page}")
	public String viewContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		// show all contact of all users
		// List<Contact> contact = contactRepository.findAll();

		// List<User> findAll = userRepository.findAll();
		/*
		 * model.addAttribute("title" ,"Show User Contact");
		 * model.addAttribute("users",findAll);
		 */
		String userName = principal.getName();
		User user = userRepository.findByEmail(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contact = contactRepository.findByUserId(user.getId(), pageable);
		// System.out.println(contact);
		model.addAttribute("users", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());

		return "normal/view_contact";
	}

	@RequestMapping("/{cId}/view_contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);

		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = userRepository.findByEmail(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {

//		Optional<Contact> optionalContact = contactRepository.findById(cId);
//		Contact contact = optionalContact.get();	
		Contact contact = contactRepository.findById(cId).get();
//		contact.setUser(null);
//		contactRepository.delete(contact);

		User user = userRepository.findByEmail(principal.getName());

		user.getContact().remove(contact);

		userRepository.save(user);

		session.setAttribute("message", new com.spring.helper.Message("Contact Delete Seccesfully...", "success"));

		return "redirect:/user/view_contact/0";
	}

	//
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("title", "Update Contact");

		Contact contact = contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	//
	@PostMapping("/process-update")
	public String processUpdate(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {
			
			if(result.hasErrors()) {
				System.out.println("ERRORS "+result.toString());
				model.addAttribute("contact",contact);
				return "normal/update_form";
			}
			
			Contact oldCotactDetail = contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				// delete photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldCotactDetail.getImage());							 
				System.err.println(file1.delete());
				
//
//				// Update new Photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldCotactDetail.getImage());
			}

			User user = userRepository.findByEmail(principal.getName());
			System.out.println("new contact" + contact);
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new com.spring.helper.Message("Your contact is updated...", "success"));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		System.out.println("Name " + contact.getName());
		System.out.println("Id " + contact.getcId());

		return "redirect:/user/" + contact.getcId() + "/view_contact";
	}

	// profile page
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		return "normal/profile";
	}
	
	//Delete User
	@GetMapping("/delete-user/{id}")
	public String deleteUser(Model model , @PathVariable("id") Integer id) {
		System.out.println("delete account");
//		model.addAttribute("title", "");
		User user = userRepository.getById(id);
		userRepository.delete(user);
		return "redirect:/";
	}
	
	//update user
	/*
	 * @PostMapping("/update-user") public String updateUser() { return
	 * "normal/update_user_form"; }
	 */
	
//	@ResponseBody
	@PostMapping("/proccess-user-update")
	public String proccessUserUpdate(@Valid @ModelAttribute("user") User user , BindingResult result, @RequestParam("profileImage") MultipartFile file , HttpSession session, Model model) {
		
		
		
		try {
			
//			if(result.hasErrors()) {
//				System.out.println("ERRORS "+result.toString());
//				model.addAttribute("user",user);
//				return "normal/user_dashboard";
//			}

			//Contact oldCotactDetail = contactRepository.findById(contact.getcId()).get();
			User oldUser = userRepository.findById(user.getId()).get();
			System.out.println("bnbn"+oldUser);
			if (!file.isEmpty()) {

				// delete photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldUser.getImageUrl());
				System.err.println(file1.delete());
//				user.setImageUrl("default.png");
				// Update new Photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(file.getOriginalFilename());

			} else {
				user.setImageUrl(oldUser.getImageUrl());
			}

			System.out.println(user.toString());
			
			userRepository.save(user);
			session.setAttribute("message", new com.spring.helper.Message("User Updated Succesfully","success"));
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "normal/user_dashboard";
	}
	
	@GetMapping("/settings")
	public String settings (Model model) {
		
		model.addAttribute("title", "Setting__Smart Contact");
//		model.addAttribute("oldPassword", "salah");
		
		return "normal/settings";
	}
	
	
//	@PostMapping("/change-password")
//	public String changePassword(@Valid @RequestParam("oldPassword") @NotBlank(message = "Old password cannot be blank") String oldPassword ,
//			@Valid @RequestParam("newPassword") @NotBlank(message = "Old password cannot be blank") String newPassword ,
//			BindingResult bindingResult ,
//			Principal principal ,
//			HttpSession session ,
//			Model model) {
//			
//		try {
//		User user = userRepository.findByEmail(principal.getName());
//		
//		if(bindingResult.hasErrors()) {
//			System.out.println("ERROR " + bindingResult.toString());
//			model.addAttribute("oldPassword",oldPassword);
//			model.addAttribute("newPassword",newPassword);
//			return "redirect:/user/settings";
//		}
//		
//		if(bCryptPasswordEncoder.matches(oldPassword , newPassword)) {
//			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
//			userRepository.save(user);
//			
//			session.setAttribute("message", new com.spring.helper.Message("Your password is successfully change...", "success"));
//		}
//		else {
//			session.setAttribute("message", new com.spring.helper.Message("Please Enter correct old password !!", "danger"));
//			return "redirect:/user/settings";
//		}
//		
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		
//			return "redirect:/user/index";
//		}
//		
//		return "redirect:/user/index";
//	}
	
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword ,@RequestParam("newPassword") String newPassword , HttpSession session , Principal principal , Model model) {
		User exitingUser = userRepository.findByEmail(principal.getName());
		
		if(oldPassword =="" || oldPassword.isEmpty()) {
			model.addAttribute("oldReq","New Password Required !");
		}
		if(newPassword == "" || newPassword.isEmpty()) {
			model.addAttribute("newReq","New Password required !");
		}
		if(bCryptPasswordEncoder.matches(oldPassword, exitingUser.getPassword())){
			if(newPassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
				exitingUser.setPassword(newPassword);
				userRepository.save(exitingUser);
				session.setAttribute("message", new com.spring.helper.Message("Your password is successfully changed.","success"));
			}
			else {
				model.addAttribute("newPassword", newPassword);
				model.addAttribute("oldPassword", oldPassword);
				model.addAttribute("newPassError", "Please use at least one uppercase letter, one lowercase letter, one digit, and one special character.");
				return "normal/settings";
			}
		}
		else {
	       session.setAttribute("message", new com.spring.helper.Message("Please enter the correct old password!","danger"));
	    	model.addAttribute("oldPass",oldPassword);
	        model.addAttribute("newPass", newPassword);
	        return "normal/settings";
	    }

		
		
		return "redirect:/user/index";
		
	}
	
	
}

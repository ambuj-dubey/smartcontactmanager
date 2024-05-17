package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	//method to hiding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME "+ userName);
		//get the user using userName(email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER  Details"+ user);
		
		model.addAttribute("user", user);
	}
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session) {
		try {
						
			String name=principal.getName();
			User user =this.userRepository.getUserByUserName(name);			//here we fetched the user
			
			//processing and uploading file
			if(file.isEmpty()) {
				//if file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.jpg");
				
			}else {
				//upload file to folder and update to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			
			contact.setUser(user);											//before this line was added I was not gettting the userid in the contact tabele
			user.getContacts().add(contact);								//then contact is added to the user
			this.userRepository.save(user);	
			System.out.println("DATA "+contact);//then save it
			System.out.println("Added to database");
			//message success
			//session.setAttribute("message", new Message("Your contact is added!! Add more..","success"));
			session.setAttribute("message", new com.smart.helper.Message("Your contact is added!! Add more..","success"));
		}catch(Exception e) {
			System.out.println("ERROR "+ e.getMessage());
			e.printStackTrace();
			//error message
			//session.setAttribute("message", new Message("SOmething went wrong.. Try Again!!..","danger"));
			session.setAttribute("message", new com.smart.helper.Message("SOmething went wrong.. Try Again!!..","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show contacts handler
	//per page = 5[n]
	//current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show Contacts");
		//contacts ki list bhejana hai
		/*this was used usinf the Principal method
		 * String userName = principal.getName(); User user =
		 * this.userRepository.getUserByUserName(userName); user.getContacts();
		 * List<Contact> contacts = user.getContacts();
		 */
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		//current page
		//contact per page -5
		Pageable pageable =  PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing particular contact detail
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model,Principal principal) {
		System.out.println("CID "+cId);
		
		Optional<Contact> contactOptional =  this.contactRepository.findById(cId);
		if(contactOptional.isEmpty()) {
			System.out.println("ID not found");
		}else {
			System.out.println("contactOptional "+contactOptional);
		}
		Contact contact = contactOptional.get();
		if(contact != null) {
			System.out.println("Contact is not null "+ contact);
		}else {
			System.out.println("Contact is null "+ contact);
		}
		String userName = principal.getName();							//this is used here for thesecurity perpose so that user can 
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId() == contact.getUser().getId()) {
			
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}
	
	//Delete contact handler 
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model,HttpSession session) {
		Optional<Contact> contactOptional =this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		contact.setUser(null);								//here I am unlinking the user from the contact becasue we have made cascade reation
		this.contactRepository.delete(contact);
		System.out.println("DELETED...");
		
		//
		session.setAttribute("message", new Message("Contact deleted succesfully....","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	
	
	
}

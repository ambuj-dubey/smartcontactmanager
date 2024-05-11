package com.smart.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

	@RequestMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager"); // This is used to provide the title as a dynamic for the home page
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager"); // This is used to provide the title as a dynamic for the about page
		return "about";
	}
	
}

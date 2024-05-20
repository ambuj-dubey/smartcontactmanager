package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact as c where c.user.id =:userId")
	//current page
	//contact per page -5
	public Page<Contact> findContactsByUser(@Param("userId") int userID, Pageable pePageable);
	
	//search ... This is created by JPA
	public List<Contact> findByNameContainingAndUser(String name,User user); //here findBy is used to create the find method and 'name' is the value which will be passed from the frontend and then Containing is a keyword which is used to check then next attribute like here User
}

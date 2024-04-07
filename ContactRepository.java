package com.spring.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.entities.Contact;
import com.spring.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{

//	@Query("from contact as c where c.user.id=:userId")
//	public List<Contact> findContactsByUser(@Param("userId") int userId);
	
	
	public Page<Contact> findByUserId(int userId , Pageable pePageable);
	
	
	public List<Contact> findByNameContainingAndUser(String name , User user);
	
}

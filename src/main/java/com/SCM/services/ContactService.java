package com.SCM.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.SCM.entities.Contact;
import com.SCM.entities.User;

public interface ContactService {
//save Contacts
Contact save(Contact contact);
//update Contact
Contact update(Contact contact);

//get contacts
List<Contact>getAll();

//get contact by id
Contact getById(String id);
//delete contact
void delete(String id);
//search contact
Page<Contact>searchByName(String nameKeyword,int size,int page,String sortBy,String order,User user);
Page<Contact>searchByEmail(String emailKeyword,int size,int page,String sortBy,String order,User user);
Page<Contact>searchByPhoneNumber(String phoneNumberKeyword,int size,int page,String sortBy,String order,User user);
//get contact by userID
List<Contact>getByUserId(String userId);

Page<Contact> getByUser(User user,int page,int size,String sortField,String sortDirection);
}

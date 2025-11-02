package com.SCM.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SCM.entities.Contact;
import com.SCM.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    // find the contacts by the userId
    // customfinder method
    Page<Contact> findByUser(User user, Pageable pageable);

    // custom query method dono me se koi bhi use kr lo
    @Query("SELECT c FROM Contact c WHERE c.user.id=:userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Page<Contact> findByUserAndNameContaining(User user, String namekeyword, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user, String emailkeyword, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phonekeyword, Pageable pageable);
}

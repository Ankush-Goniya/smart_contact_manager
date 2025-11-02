package com.SCM.Controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.SCM.entities.Contact;
import com.SCM.entities.User;
import com.SCM.forms.ContactForm;
import com.SCM.forms.ContactSearchForm;
import com.SCM.helper.AppConstants;
import com.SCM.helper.Helper;
import com.SCM.helper.Message;
import com.SCM.helper.MessageType;
import com.SCM.services.ContactService;
import com.SCM.services.IMAGESERV;

import com.SCM.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.UUID;

import java.util.List;
import org.slf4j.Logger;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(ContactController.class);
    @Autowired
    private ContactService contactService;
    @Autowired
    private IMAGESERV imageService;
    // add contact page handler

    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        // contactForm.setName("Ankush kumar");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication,
            HttpSession session) {
        // process the form data

        // validate the form
        if (result.hasErrors()) {

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }
        String username = Helper.getEmailOfLoggedinUser(authentication);
        // form --->contact
        User user = userService.getUserByEmail(username);
        // process the contect picture
        logger.info("file information:{}", contactForm.getContactImage().getOriginalFilename());

        // image upload krne ka code
        // String filename = UUID.randomUUID().toString();
        // String fileURL = imageService.uploadImage(contactForm.getContactImage(),
        // filename);
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
        contactService.save(contact);

        System.out.println(contactForm);
        // set the contect picture url

        // set message to be displayed on the view
        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts/add";
    }

    // view contacts
    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {
        // load all the user contact
        String username = Helper.getEmailOfLoggedinUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search handler
    @GetMapping("/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        System.out.println("this is for search checking");
        logger.info("field{}  keyword{}", contactSearchForm.getField(), contactSearchForm.getValue());
        var user = userService.getUserByEmail(Helper.getEmailOfLoggedinUser(authentication));
        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }
        logger.info("pageContact {}", pageContact);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    }

    // delete contact
    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {

        contactService.delete(contactId);
        System.out.println("contactId is deleted" + contactId);
        session.setAttribute("message",
                Message.builder().content("contact is deleted successfully !!").type(MessageType.green).build());
        return "redirect:/user/contacts";
    }

    // update contact view
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable("contactId") String contactId, Model model) {

        var contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);
        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult,
            Model model) {

        // @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
        // public String updateContact(@PathVariable("contactId") String contactId,
        // @Valid @ModelAttribute ContactForm contactForm,
        // BindingResult bindingResult,
        // Model model) {

        // update the contact
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        // update the contact
        var con = contactService.getById(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());
        // con.setPicture(contactForm.getPicture());

        // process image

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);

        } else {
            logger.info("file is empty");
        }
        // end process image

        var updateCon = contactService.update(con);
        logger.info("updated contact {}", updateCon);
        model.addAttribute("message", Message.builder().content("Contact Updated !!").type(MessageType.green).build());
        return "redirect:/user/contacts/view/" + contactId;
    }
}

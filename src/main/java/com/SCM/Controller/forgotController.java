package com.SCM.Controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SCM.entities.User;
import com.SCM.repositories.UserRepo;


import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class forgotController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    Random random = new Random();

    // Step 1: Open forgot password page
    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    // Step 2: Send OTP to email
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
        System.out.println("EMAIL: " + email);

        // Generate 6-digit random OTP
        int otp = random.nextInt(999999);
        System.out.println("Generated OTP: " + otp);

        // Send OTP via email
        String subject = "OTP from Smart Contact Manager";
        String message = "Dear User,\n\nYour OTP for password reset is: " + otp + "\n\nPlease do not share this OTP.";
        String to = email;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        try {
            mailSender.send(mailMessage);
            System.out.println("OTP Email sent successfully...");

            session.setAttribute("otp", otp);
            session.setAttribute("email", email);

            model.addAttribute("message", "OTP sent successfully to your email!");
            return "verify_otp"; // next page

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to send OTP. Please check your email address.");
            return "forgot_email_form";
        }
    }

    // Step 3: Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {
        int sessionOtp = (int) session.getAttribute("otp");
        String email = (String) session.getAttribute("email");

        if (sessionOtp == otp) {
            // check if user exists
           Optional<User> optionalUser = userRepo.findByEmail(email);

if (optionalUser.isPresent()) {
    User user = optionalUser.get();
    // do your logic with user
} else {
    model.addAttribute("error", "User with this email does not exist.");
    return "forgot_email_form";
}

            // go to password change form
            return "password_change_form";
        } else {
            model.addAttribute("error", "Invalid OTP, please try again!");
            return "verify_otp";
        }
    }

    // Step 4: Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword,
                                 HttpSession session,
                                 Model model) {
        String email = (String) session.getAttribute("email");
       Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser == null) {
            model.addAttribute("error", "User not found. Try again.");
            return "forgot_email_form";
        }

        User user=optionalUser.get();
       user.setPassword(passwordEncoder.encode(newpassword));
        userRepo.save(user);

        // clear session
        session.removeAttribute("otp");
        session.removeAttribute("email");

        model.addAttribute("message", "Password changed successfully. You can now login!");
        return "login";
    }
}

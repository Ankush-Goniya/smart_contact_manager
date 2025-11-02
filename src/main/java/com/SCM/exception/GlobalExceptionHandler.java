// package com.SCM.exception;

// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.multipart.MaxUploadSizeExceededException;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @ControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(MaxUploadSizeExceededException.class)
//     public String handleMaxSizeException(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
//         redirectAttributes.addFlashAttribute("error", "⚠️ File too large! Please upload an image smaller than 20 MB.");
//         return "redirect:/user/contacts/add"; // redirect back to your form
//     }
// }

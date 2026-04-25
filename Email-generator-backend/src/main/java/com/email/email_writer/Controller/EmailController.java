package com.email.email_writer.Controller;


import com.email.email_writer.Dto.EmailRequest;
import com.email.email_writer.Service.EmailGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin("*")
public class EmailController {

    @Autowired
    private EmailGeneratorService emailGeneratorService;

     @PostMapping("/generate")
     public ResponseEntity<String> generateEmailResponse(@RequestBody EmailRequest emailRequest){
         String response = emailGeneratorService.emailResponse(emailRequest);
          return ResponseEntity.ok(response);
     }


}

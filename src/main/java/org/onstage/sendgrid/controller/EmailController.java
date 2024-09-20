package org.onstage.sendgrid.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.sendgrid.SendGridService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class EmailController {
    private final SendGridService sendGridService;

    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmail(@RequestParam(name = "email") String email) {
        sendGridService.sendTestEmail(email);
        return ResponseEntity.ok().build();
    }
}

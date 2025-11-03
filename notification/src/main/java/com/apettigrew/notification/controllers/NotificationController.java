package com.apettigrew.notification.controllers;


import com.apettigrew.notification.constant.JsonApiConstants;
import com.apettigrew.notification.dto.ContactInfoDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
@AllArgsConstructor
@Validated
public class NotificationController {
    @Autowired
    private ContactInfoDto contactInfoDto;

    @GetMapping("/contact-info")
    public ResponseEntity<ContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactInfoDto);
    }
}

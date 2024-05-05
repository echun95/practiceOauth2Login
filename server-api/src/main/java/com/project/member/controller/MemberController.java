package com.project.member.controller;

import com.project.entity.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @GetMapping("/test")
    public ResponseEntity<Test> test(){
        Test test = Test.builder()
                .testId(1L)
                .name("test")
                .build();
        return new ResponseEntity<>(test, HttpStatus.OK);
    }
}

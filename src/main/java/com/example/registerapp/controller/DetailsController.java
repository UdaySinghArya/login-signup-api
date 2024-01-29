package com.example.registerapp.controller;

import com.example.registerapp.entity.Details;
import com.example.registerapp.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class DetailsController {
    @Autowired
    private UserDetailService userDetailService;

    @PostMapping("details")
    public boolean sendDetails(@RequestBody Details details){
        return userDetailService.send(details);
    }
    
}

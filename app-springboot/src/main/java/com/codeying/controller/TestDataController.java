package com.codeying.controller;

import com.codeying.entity.AdmissionProject;
import com.codeying.service.AdmissionProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/data")
public class TestDataController {

    @Autowired
    private AdmissionProjectService admissionProjectService;

    @GetMapping("/projects")
    public List<AdmissionProject> listProjects() {
        return admissionProjectService.list();
    }
}

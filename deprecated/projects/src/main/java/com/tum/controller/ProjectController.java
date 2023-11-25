package com.tum.controller;


import com.tum.model.Project;
import com.tum.service.ProjectService;
import constant.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public List<Project> getAllProjects() {
        System.out.println("projectService.getAllProjects()");
        return projectService.getAllProjects();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // send CSRF token to frontend, used in POST,DELETE,PUT requests
    @GetMapping("/csrf")
    public ResponseEntity<String> csrf() throws Exception {
        return new ResponseEntity<String>("{}", HttpStatus.OK);
    }
}

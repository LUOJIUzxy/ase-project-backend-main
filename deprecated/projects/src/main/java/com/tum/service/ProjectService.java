package com.tum.service;

import com.tum.model.Project;
import com.tum.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(Project project){
        projectRepository.save(project);
        return project;
    }

    public Project findByName(String name) {
        return projectRepository.findByName(name);
    }

    public List<Project> getAllProjects(){
        System.out.println(projectRepository.findAll());
        return projectRepository.findAll();
    }
}
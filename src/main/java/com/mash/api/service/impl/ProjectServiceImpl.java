package com.mash.api.service.impl;


import com.mash.api.entity.Project;
import com.mash.api.repository.ProjectRepository;
import com.mash.api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Page<Project> findAll(Specification<Project> specification, Pageable pageable) {
        return projectRepository.findAll(specification, pageable);
    }

    @Override
    public Project findById(Integer id) {
        return projectRepository.findOne(id);
    }

    @Override
    public void deleteById(Integer id) {
        projectRepository.delete(id);
    }
}

package com.shubham.taskflow.service;

import com.shubham.taskflow.dto.ProjectRequest;
import com.shubham.taskflow.dto.ProjectResponse;
import com.shubham.taskflow.exception.ResourceNotFoundException;
import com.shubham.taskflow.model.Project;
import com.shubham.taskflow.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project(request.getName(), request.getDescription());
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(getProjectOrThrow(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProjectOrThrow(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return toResponse(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = getProjectOrThrow(id);
        projectRepository.delete(project);
    }

    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getTasks().size()
        );
    }
}

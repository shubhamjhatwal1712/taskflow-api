package com.shubham.taskflow.service;

import com.shubham.taskflow.dto.TaskRequest;
import com.shubham.taskflow.dto.TaskResponse;
import com.shubham.taskflow.exception.ResourceNotFoundException;
import com.shubham.taskflow.model.Project;
import com.shubham.taskflow.model.Task;
import com.shubham.taskflow.model.TaskStatus;
import com.shubham.taskflow.repository.ProjectRepository;
import com.shubham.taskflow.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + request.getProjectId()));

        Task task = new Task();
        applyRequest(task, request);
        task.setProject(project);

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(Long projectId, TaskStatus status, Pageable pageable) {
        Page<Task> page;
        if (projectId != null && status != null) {
            page = taskRepository.findByProjectIdAndStatus(projectId, status, pageable);
        } else if (projectId != null) {
            page = taskRepository.findByProjectId(projectId, pageable);
        } else if (status != null) {
            page = taskRepository.findByStatus(status, pageable);
        } else {
            page = taskRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return toResponse(getTaskOrThrow(id));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTaskOrThrow(id);

        if (!task.getProject().getId().equals(request.getProjectId())) {
            Project newProject = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project not found with id: " + request.getProjectId()));
            task.setProject(newProject);
        }

        applyRequest(task, request);
        return toResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }

    private void applyRequest(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
    }

    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}

package com.reverse.nsu.service;

import com.reverse.nsu.dto.ProjectResponseDto;
import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectStatus;
import com.reverse.nsu.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectAdminService {

    private final ProjectRepository projectRepository;

    /**
     * Admin 1. 프로젝트 전체 목록 일괄 확인 (모니터링용)
     */
    public Page<ProjectResponseDto> getAllProjectsForAdmin(Pageable pageable) {
        Page<Project> projects = projectRepository.findAllByOrderByCreatedDateDesc(pageable);
        return projects.map(ProjectResponseDto::new);
    }

    /**
     * Admin 2. 프로젝트 강제 종료 처리
     */
    @Transactional
    public void closeProjectForce(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        project.updateStatus(ProjectStatus.CLOSED);
    }

    /**
     * Admin 3. 프로젝트 강제 삭제 처리
     */
    @Transactional
    public void deleteProjectForce(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        projectRepository.delete(project);
    }
}
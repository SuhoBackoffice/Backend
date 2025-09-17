package baekgwa.suhoserver.domain.project.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectReadService
 * Author      : Baekgwa
 * Date        : 2025-09-15
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-15     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class ProjectReadService {

	private final ProjectRepository projectRepository;
	private final ProjectBranchRepository projectBranchRepository;
	private final ProjectStraightRepository projectStraightRepository;

	/**
	 * projectId 로, 프로젝트 정보 조회
	 * @param projectId 프로젝트 PK
	 * @return 찾은 프로젝트 Entity
	 */
	@Transactional(readOnly = true)
	public ProjectEntity getProjectOrThrow(Long projectId) {
		return projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));
	}

	/**
	 * 프로젝트에 저장된 분기레일 정보 조회
	 * @param project 프로젝트 Entity
	 * @return 분기레일 List
	 */
	@Transactional(readOnly = true)
	public List<ProjectBranchEntity> getProjectBranchInfoListOrThrow(ProjectEntity project) {
		return projectBranchRepository.findByProject(project);
	}

	/**
	 * 프로젝트에 저장된 직선레일 정보 조회
	 * @param project 프로젝트 Entity
	 * @return 직선레일 List
	 */
	@Transactional(readOnly = true)
	public List<ProjectStraightEntity> getProjectStraightListOrThrow(ProjectEntity project) {
		return projectStraightRepository.findByProject(project);
	}

	/**
	 * 프로젝트 페이징 조회
	 * @param dto 페이징 정보
	 * @return 찾은 프로젝트 페이징 리스트
	 */
	@Transactional(readOnly = true)
	public PageResponse<ProjectResponse.ProjectInfo> getProjectInfoListOrThrow(ProjectRequest.GetProjectInfo dto) {
		// 1. 페이지네이션 파라미터 유효성 검증
		if (dto.getPage() < 0 || dto.getSize() < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		// 2. StartDate, EndDate 검증
		if (dto.getStartDate() != null &&
			dto.getEndDate() != null &&
			!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 3. list 조회
		Page<ProjectResponse.ProjectInfo> findData = projectRepository.searchProjectList(dto);

		return PageResponse.of(findData);
	}
}

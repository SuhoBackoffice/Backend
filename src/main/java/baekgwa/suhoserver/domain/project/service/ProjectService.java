package baekgwa.suhoserver.domain.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.domain.project.service
 * FileName    : ProjectService
 * Author      : Baekgwa
 * Date        : 2025-08-07
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-07     Baekgwa               Initial creation
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final VersionInfoRepository versionInfoRepository;

	@Transactional
	public ProjectResponse.NewProjectDto createNewProject(ProjectRequest.PostNewProjectDto postNewProjectDto) {
		// 1. 입력값 유효성 검증
		if(postNewProjectDto.getStartDate() != null &&
			postNewProjectDto.getEndDate() != null &&
			!postNewProjectDto.getEndDate().isAfter(postNewProjectDto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version info entity 조회 및 유효성 검증
		VersionInfoEntity findVersion = versionInfoRepository.findById(postNewProjectDto.getVersionId())
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 3. Entity 생성 및 저장
		ProjectEntity newProject = ProjectEntity.createNewProject(findVersion, postNewProjectDto.getRegion(),
			postNewProjectDto.getStartDate(), postNewProjectDto.getEndDate());
		ProjectEntity savedProject = projectRepository.save(newProject);

		return new ProjectResponse.NewProjectDto(savedProject.getId());
	}
}

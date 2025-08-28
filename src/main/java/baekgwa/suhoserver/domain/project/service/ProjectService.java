package baekgwa.suhoserver.domain.project.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baekgwa.suhoserver.domain.project.dto.ProjectRequest;
import baekgwa.suhoserver.domain.project.dto.ProjectResponse;
import baekgwa.suhoserver.global.exception.GlobalException;
import baekgwa.suhoserver.global.response.ErrorCode;
import baekgwa.suhoserver.global.response.PageResponse;
import baekgwa.suhoserver.model.branch.type.entity.BranchTypeEntity;
import baekgwa.suhoserver.model.branch.type.repository.BranchTypeRepository;
import baekgwa.suhoserver.model.project.branch.entity.ProjectBranchEntity;
import baekgwa.suhoserver.model.project.branch.repository.ProjectBranchRepository;
import baekgwa.suhoserver.model.project.project.entity.ProjectEntity;
import baekgwa.suhoserver.model.project.project.repository.ProjectRepository;
import baekgwa.suhoserver.model.project.straight.entity.ProjectStraightEntity;
import baekgwa.suhoserver.model.project.straight.repository.ProjectStraightRepository;
import baekgwa.suhoserver.model.straight.type.entity.StraightTypeEntity;
import baekgwa.suhoserver.model.straight.type.repository.StraightTypeRepository;
import baekgwa.suhoserver.model.version.entity.VersionInfoEntity;
import baekgwa.suhoserver.model.version.repository.VersionInfoRepository;
import jakarta.validation.constraints.NotNull;
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
	private final ProjectBranchRepository projectBranchRepository;
	private final BranchTypeRepository branchTypeRepository;
	private final StraightTypeRepository straightTypeRepository;
	private final VersionInfoRepository versionInfoRepository;
	private final ProjectStraightRepository projectStraightRepository;

	private static final BigDecimal LITZ_WIRE_MAX = BigDecimal.valueOf(1800L);
	private static final BigDecimal OFFSET_215 = BigDecimal.valueOf(215L);
	private static final BigDecimal TH_1200 = BigDecimal.valueOf(1200L);
	private static final BigDecimal TH_2400 = BigDecimal.valueOf(2400L);
	private static final BigDecimal TH_3600 = BigDecimal.valueOf(3600L);

	@Transactional
	public ProjectResponse.NewProjectDto createNewProject(ProjectRequest.PostNewProjectDto postNewProjectDto) {
		// 1. 입력값 유효성 검증
		if (postNewProjectDto.getStartDate() != null &&
			postNewProjectDto.getEndDate() != null &&
			!postNewProjectDto.getEndDate().isAfter(postNewProjectDto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version info entity 조회 및 유효성 검증
		VersionInfoEntity findVersion = versionInfoRepository.findById(postNewProjectDto.getVersionId())
			.orElseThrow(
				() -> new GlobalException(ErrorCode.NOT_FOUND_VERSION));

		// 3. Entity 생성 및 저장
		ProjectEntity newProject = ProjectEntity.createNewProject(findVersion, postNewProjectDto.getName(),
			postNewProjectDto.getRegion(), postNewProjectDto.getStartDate(), postNewProjectDto.getEndDate());
		ProjectEntity savedProject = projectRepository.save(newProject);

		return new ProjectResponse.NewProjectDto(savedProject.getId());
	}

	@Transactional
	public ProjectResponse.NewProjectDto registerProjectBranch(
		List<ProjectRequest.PostProjectBranchInfo> postProjectBranchInfoList, Long projectId
	) {
		// 1. 프로젝트 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. branch 정보 조회
		List<Long> branchIdList = postProjectBranchInfoList.stream()
			.map(ProjectRequest.PostProjectBranchInfo::getBranchTypeId)
			.distinct()
			.toList();
		List<BranchTypeEntity> findBranchTypeList = branchTypeRepository.findAllById(branchIdList);

		// 2-1. 현재 프로젝트의 모든 분기 레일 정보를 불러와서, 이미 있는 분기 레일을 또 추가하려는 건 아닌지 검증.
		List<Long> findBranchTypeIdList = findBranchTypeList.stream().map(BranchTypeEntity::getId).toList();
		List<String> existBranchCode = projectBranchRepository.findAllByBranchTypeIdIn(findBranchTypeIdList)
			.stream().map(data -> data.getBranchType().getCode()).toList();

		// 2-2. 추가 검증. 찾아온 branchType 이 id List 보다 작으면, 잘못된 id가 입력된 경우. (없는 것)
		if (branchIdList.size() > findBranchTypeList.size())
			throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);

		// 2-3. branch 정보 stream 으로 map 처리
		Map<Long, BranchTypeEntity> findBranchTypeMap = findBranchTypeList.stream()
			.collect(Collectors.toMap(BranchTypeEntity::getId, Function.identity()));

		// 3. ProjectBranch Entity List 생성
		List<ProjectBranchEntity> newProjectBranchList = postProjectBranchInfoList.stream()
			.map(dto -> {
				BranchTypeEntity branchType = findBranchTypeMap.get(dto.getBranchTypeId());
				if (branchType == null) {
					throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH_TYPE);
				}
				if (!findProject.getVersionInfoEntity().getId().equals(branchType.getVersionInfoEntity().getId())) {
					throw new GlobalException(ErrorCode.INVALID_VERSION_BRANCH);
				}
				if(existBranchCode.contains(branchType.getCode())) {
					throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_BRANCH_DATA);
				}
				return ProjectBranchEntity.createNewProjectBranch(findProject, branchType, dto.getQuantity());
			}).toList();

		// 4. 전체 저장
		projectBranchRepository.saveAll(newProjectBranchList);

		return new ProjectResponse.NewProjectDto(projectId);
	}

	@Transactional(readOnly = true)
	public ProjectResponse.ProjectDetailInfo getProjectInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. dto 변환 후 반환
		return ProjectResponse.ProjectDetailInfo.of(findProject);
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectBranchInfo> getProjectBranchInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. Project 의 Branch 정보 조회 및 응답
		List<ProjectBranchEntity> findProjectBranchList = projectBranchRepository.findByProject(findProject);
		return findProjectBranchList.stream()
			.map(ProjectResponse.ProjectBranchInfo::of)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse.ProjectStraightInfo> getProjectStraightInfo(Long projectId) {
		// 1. Project 유효성 검증 및 Data 조회
		ProjectEntity findProject = projectRepository.findById(projectId).orElseThrow(
			() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. Project 의 Straight 정보 조회
		List<ProjectStraightEntity> findProjectStraightList = projectStraightRepository.findByProject(findProject);
		return findProjectStraightList.stream()
			.map(data -> {
				BigDecimal holePosition = calcHolePosition(data);
				ProjectResponse.LitzInfo litzInfo;
				litzInfo = generateLitzInfoList(data);
				return ProjectResponse.ProjectStraightInfo.of(data, litzInfo, holePosition);
			})
			.toList();
	}

	@Transactional
	public void registerProjectStraight(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList, Long projectId
	) {
		// 1. 프로젝트 정보 조회
		ProjectEntity findProject = projectRepository.findById(projectId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_PROJECT));

		// 2. 입력 데이터 중복 및 기타 데이터 중복 검증.
		validateDuplicationStraight(postProjectStraightInfoList, findProject);

		// 3. straightTypeId 으로, 필요한 StraightType List 조회
		List<Long> straightTypeIdList = postProjectStraightInfoList.stream()
			.map(ProjectRequest.PostProjectStraightInfo::getStraightTypeId)
			.distinct()
			.toList();
		List<StraightTypeEntity> findStraightTypeList = straightTypeRepository.findAllById(straightTypeIdList);

		// 3-1. id 기반으로, 검색 가능하도록 Map 으로 convert
		Map<Long, StraightTypeEntity> findStraightTypeMap =
			findStraightTypeList.stream().collect(Collectors.toMap(StraightTypeEntity::getId, Function.identity()));

		// 4. ProjectStraightEntity List 생성 및 저장
		List<ProjectStraightEntity> projectStraightList =
			getProjectStraightList(postProjectStraightInfoList, findStraightTypeMap, findProject);

		// 5. 저장 및 반환
		projectStraightRepository.saveAll(projectStraightList);
	}

	@Transactional(readOnly = true)
	public PageResponse<ProjectResponse.ProjectInfo> getProjectInfoList(ProjectRequest.GetProjectInfo dto) {

		// 1. 페이지네이션 파라미터 유효성 검증
		if (dto.getPage() < 0 || dto.getSize() < 1) {
			throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}

		// 1-1. StartDate, EndDate 검증
		if (dto.getStartDate() != null &&
			dto.getEndDate() != null &&
			!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new GlobalException(ErrorCode.PROJECT_END_AFTER_START_ERROR);
		}

		// 2. version 유효성 검증
		if (dto.getVersionId() != null && !versionInfoRepository.existsById(dto.getVersionId())) {
			throw new GlobalException(ErrorCode.NOT_FOUND_VERSION);
		}

		// 3. list 조회
		Page<ProjectResponse.ProjectInfo> findData = projectRepository.searchProjectList(dto);

		return PageResponse.of(findData);
	}

	@Transactional
	public void deleteProjectStraight(Long projectStraightId) {
		if (!projectStraightRepository.existsById(projectStraightId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT);
		}
		projectStraightRepository.deleteById(projectStraightId);
	}

	@Transactional
	public void deleteProjectBranch(Long projectBranchId) {
		if (!projectBranchRepository.existsById(projectBranchId)) {
			throw new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH);
		}
		projectBranchRepository.deleteById(projectBranchId);
	}

	@Transactional
	public void patchProjectStraight(Long projectStraightId, ProjectRequest.PatchProjectStraightDto dto) {
		// 1. projectStraight Entity 조회
		ProjectStraightEntity findProjectStraight = projectStraightRepository.findById(projectStraightId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_STRAIGHT));

		// 2. 업데이트 처리
		findProjectStraight.patchProjectStraight(dto.getTotalQuantity());
	}

	@Transactional
	public void patchProjectBranch(Long projectBranchId, ProjectRequest.PatchProjectBranchDto dto) {
		// 1. projectBranch Entity 조회
		ProjectBranchEntity findProjectBranch = projectBranchRepository.findById(projectBranchId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_EXIST_PROJECT_BRANCH));

		// 2. 업데이트 처리
		findProjectBranch.patchProjectBranch(dto.getTotalQuantity());
	}

	private @NotNull BigDecimal calcHolePosition(ProjectStraightEntity projectStraight) {
		// 1. 루프레일이 아닌 경우
		if (Boolean.FALSE.equals(projectStraight.getIsLoopRail())) {
			return BigDecimal.ZERO;
		}

		// 2. 데이터 추출
		String type = projectStraight.getStraightType().getType().substring(0, 1).toUpperCase();
		BigDecimal loopLitzWire = projectStraight.getProject().getVersionInfoEntity().getLoopLitzWire();
		Long length = projectStraight.getLength();

		// 3. 가공 위치 계산
		return switch (type) {
			case "C" -> BigDecimal.valueOf(length)
				.divide(BigDecimal.valueOf(2));
			case "E" -> BigDecimal.valueOf(length)
				.subtract(loopLitzWire); // End: length - loopLitzWire
			case "S" -> BigDecimal.valueOf(length)
				.subtract(BigDecimal.valueOf(215))
				.subtract(loopLitzWire); // ✅ Side: length - 215 - loopLitzWire
			default -> BigDecimal.ZERO;
		};
	}

	private @NotNull ProjectResponse.LitzInfo generateLitzInfoList(ProjectStraightEntity projectStraight) {
		// OffsetType 및 LoopType 추출
		String rawType = projectStraight.getStraightType().getType().toUpperCase();
		boolean loop = Boolean.TRUE.equals(projectStraight.getIsLoopRail());

		String offsetType;
		String loopType;
		if (loop) {
			if (rawType.length() >= 2) {
				// "CA" -> "A", "EC" -> "C"
				loopType = rawType.substring(0, 1);
				offsetType = rawType.substring(1, 2);
			} else {
				throw new GlobalException(ErrorCode.INVALID_LOOP_RAIL_TYPE_DATA);
			}
		} else {
			loopType = null;
			offsetType = rawType;
		}

		// 1. baseLitzWire 계산
		long length = projectStraight.getLength();
		BigDecimal loopLitzWire = projectStraight.getProject().getVersionInfoEntity().getLoopLitzWire();
		Map<Integer, BigDecimal> baseLitzWireMap = Boolean.FALSE.equals(projectStraight.getIsLoopRail())
			? baseLitzWireSupporter(length)
			: baseLoopLitzWireSupporter(length, loopType, loopLitzWire);

		// 2. 타입에 따라 offset 되어야 할 번호 확인
		Map<LitzWireAnchor, Integer> anchorMap = pickAnchors(baseLitzWireMap);

		switch (offsetType) {
			case "B" -> dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
			case "C" -> dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
			case "D" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RU));
			}
			case "E" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RD));
			}
			case "F" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.RU));
			}
			case "G" -> {
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LU));
				dec(baseLitzWireMap, anchorMap.get(LitzWireAnchor.LD));
			}
			case "A" -> { /* no-op */ }
			default -> { /* 변경 없음 */ }
		}

		return ProjectResponse.LitzInfo.from(baseLitzWireMap);
	}

	private Map<LitzWireAnchor, Integer> pickAnchors(Map<Integer, BigDecimal> baseLitzWire) {
		int ru;
		if (present(baseLitzWire, 5)) {
			ru = 5;
		} else if (present(baseLitzWire, 3)) {
			ru = 3;
		} else {
			ru = 1;
		}

		int rd;
		if (present(baseLitzWire, 6)) {
			rd = 6;
		} else if (present(baseLitzWire, 4)) {
			rd = 4;
		} else {
			rd = 2;
		}

		return Map.of(
			LitzWireAnchor.LU, 1,
			LitzWireAnchor.LD, 2,
			LitzWireAnchor.RU, ru,
			LitzWireAnchor.RD, rd
		);
	}

	private boolean present(Map<Integer, BigDecimal> m, int idx) {
		return m.getOrDefault(idx, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0;
	}

	private Map<Integer, BigDecimal> baseLoopLitzWireSupporter(
		Long length, String loopType, BigDecimal loopLitzWire
	) {
		BigDecimal len = BigDecimal.valueOf(length);
		BigDecimal loop = loopLitzWire; // scale=1 가정
		Map<Integer, BigDecimal> m = new HashMap<>();

		switch (loopType) {
			case "C" -> {
				BigDecimal perSide = len.subtract(loop.multiply(BigDecimal.valueOf(2L)))
					.divide(BigDecimal.valueOf(2L), 1, java.math.RoundingMode.HALF_UP);
				perSide = perSide.max(BigDecimal.ZERO);
				m.put(1, perSide);
				m.put(2, perSide);
				m.put(3, perSide);
				m.put(4, perSide);
			}
			case "E" -> {
				BigDecimal leftBase;
				if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
					leftBase = len;
				} else if (len.compareTo(TH_2400) <= 0) {
					leftBase = TH_1200;
				} else {
					leftBase = LITZ_WIRE_MAX;
				}
				BigDecimal remaining = len.subtract(leftBase).max(BigDecimal.ZERO);
				BigDecimal right = remaining.subtract(loop.multiply(BigDecimal.valueOf(2L))).max(BigDecimal.ZERO);
				m.put(1, leftBase);
				m.put(2, leftBase);
				if (right.signum() > 0) {
					m.put(3, right);
					m.put(4, right);
				}
			}
			case "S" -> {
				BigDecimal leftBase = len.min(LITZ_WIRE_MAX);
				BigDecimal remaining = len.subtract(leftBase);
				BigDecimal deduct = loop.multiply(BigDecimal.valueOf(2L)).add(OFFSET_215);

				if (remaining.signum() > 0) {
					BigDecimal right = remaining.subtract(deduct).max(BigDecimal.ZERO);
					m.put(1, leftBase);
					m.put(2, leftBase);
					m.put(3, right);
					m.put(4, right);
					m.put(5, OFFSET_215);
					m.put(6, OFFSET_215);
				} else {
					BigDecimal left = leftBase.subtract(deduct).max(BigDecimal.ZERO);
					m.put(1, left);
					m.put(2, left);
					m.put(3, OFFSET_215);
					m.put(4, OFFSET_215);
				}
			}
			default -> {
				return baseLitzWireSupporter(length);
			}
		}
		return m;
	}

	private Map<Integer, BigDecimal> baseLitzWireSupporter(long length) {
		BigDecimal len = BigDecimal.valueOf(length);
		Map<Integer, BigDecimal> m = new HashMap<>();

		if (len.compareTo(LITZ_WIRE_MAX) <= 0) {
			m.put(1, len);
			m.put(2, len);
		} else if (len.compareTo(TH_2400) <= 0) {
			BigDecimal rest = len.subtract(TH_1200);
			m.put(1, TH_1200);
			m.put(2, TH_1200);
			m.put(3, rest);
			m.put(4, rest);
		} else { // <= 3600
			BigDecimal rest = len.subtract(LITZ_WIRE_MAX);
			m.put(1, LITZ_WIRE_MAX);
			m.put(2, LITZ_WIRE_MAX);
			m.put(3, rest);
			m.put(4, rest);
		}
		return m;
	}

	// litzWire 차감 진행
	private void dec(Map<Integer, BigDecimal> m, int idx) {
		BigDecimal v = m.getOrDefault(idx, BigDecimal.ZERO);
		v = v.subtract(OFFSET_215).max(BigDecimal.ZERO);
		m.put(idx, v);
	}

	private static List<ProjectStraightEntity> getProjectStraightList(
		List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		Map<Long, StraightTypeEntity> findStraightTypeMap, ProjectEntity findProject) {
		return postProjectStraightInfoList.stream().map(
				dto -> {
					StraightTypeEntity findStraightType = findStraightTypeMap.get(dto.getStraightTypeId());
					if (findStraightType == null) {
						throw new GlobalException(ErrorCode.NOT_FOUND_STRAIGHT_TYPE);
					}
					if (!Objects.equals(findStraightType.getIsLoopRail(), dto.getIsLoopRail())) {
						if (Boolean.TRUE.equals(dto.getIsLoopRail())) {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_LOOP_TYPE);
						} else {
							throw new GlobalException(ErrorCode.NOT_MATCH_STRAIGHT_NORMAL_TYPE);
						}
					}
					return ProjectStraightEntity
						.createNewStraight(findProject, findStraightType, dto.getTotalQuantity(), dto.getIsLoopRail(),
							dto.getLength());
				})
			.toList();
	}

	private void validateDuplicationStraight(List<ProjectRequest.PostProjectStraightInfo> postProjectStraightInfoList,
		ProjectEntity findProject) {

		// 1-0. 요청 내부 중복 차단용 record
		record StraightKey(Long length, Long straightTypeId) {
		}

		Set<StraightKey> requestKeySet = new HashSet<>();
		for (ProjectRequest.PostProjectStraightInfo dto : postProjectStraightInfoList) {
			StraightKey key = new StraightKey(dto.getLength(), dto.getStraightTypeId());
			if (!requestKeySet.add(key)) {
				throw new GlobalException(ErrorCode.INVALID_PROJECT_STRAIGHT_REGISTER_DATA_DUPLICATION);
			}
		}

		// 1-1. DB 중복 차단: 해당 프로젝트의 기존 (length, straightTypeId)와 교집합 확인
		List<ProjectStraightEntity> existing = projectStraightRepository.findByProject(findProject);
		Set<StraightKey> existingKeySet = existing.stream()
			.map(e -> new StraightKey(e.getLength(), e.getStraightType().getId()))
			.collect(Collectors.toSet());

		for (StraightKey k : requestKeySet) {
			if (existingKeySet.contains(k)) {
				throw new GlobalException(ErrorCode.ALREADY_EXIST_PROJECT_STRAIGHT_DATA);
			}
		}
	}
}

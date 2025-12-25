package baekgwa.suhoserver.model.project.branch.serial.entity;

import baekgwa.suhoserver.global.entity.TemporalEntity;
import baekgwa.suhoserver.global.factory.ProductSerialFactory;
import baekgwa.suhoserver.model.project.ProductInactiveReason;
import baekgwa.suhoserver.model.project.ProductSerialState;
import baekgwa.suhoserver.model.project.branch.branch.entity.ProjectBranchEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.model.project.branch.serial.entity
 * FileName    : ProjectBranchSerialEntity
 * Author      : Baekgwa
 * Date        : 25. 12. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 12. 25.     Baekgwa               Initial creation
 */
@Entity
@Getter
@Table(name = "project_branch_serial")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectBranchSerialEntity extends TemporalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "serial", nullable = false)
	private String serial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_branch_id", nullable = false)
	private ProjectBranchEntity projectBranch;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private ProductSerialState state;

	@Enumerated(EnumType.STRING)
	@Column(name = "reason", nullable = false)
	private ProductInactiveReason reason;

	@Column(name = "sequence", nullable = false)
	private Long sequence;

	@Builder
	private ProjectBranchSerialEntity(String serial, ProjectBranchEntity projectBranch, ProductSerialState state,
		ProductInactiveReason reason, Long sequence) {
		this.serial = serial;
		this.projectBranch = projectBranch;
		this.state = state;
		this.reason = reason;
		this.sequence = sequence;
	}

	public static ProjectBranchSerialEntity of(ProjectBranchEntity projectBranch, long sequence) {
		String serial = ProductSerialFactory.generateBranchSerial(projectBranch.getBranchType().getCode(), sequence);

		return ProjectBranchSerialEntity
			.builder()
			.serial(serial)
			.projectBranch(projectBranch)
			.state(ProductSerialState.ACTIVE)
			.sequence(sequence)
			.build();
	}
}

/**
 * FileName    : V3.0.2__create_work_report_domain.sql
 * Author      : Baekgwa
 * Date        : 2025-12-29
 * Description : 업무 보고를 위한 테이블 생성
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-12-29     Baekgwa               Initial creation
 */

CREATE TABLE `work_report`
(
    `id`               BIGINT AUTO_INCREMENT NOT NULL,
    `report_user_id`   BIGINT                NOT NULL,
    `report_user_name` VARCHAR(50)           NOT NULL,
    `project_id`       BIGINT                NOT NULL,
    `work_summary`     TEXT                  NULL,
    `work_date`        DATE                  NOT NULL,
    `status`           VARCHAR(50)           NOT NULL,
    `reject_reason`    VARCHAR(50)           NULL,
    -- PENDING / APPROVED 일 때만 유니크 제약을 적용하기 위한 컬럼
    `active_flag` TINYINT(1) GENERATED ALWAYS AS (
        CASE
            WHEN status IN ('PENDING', 'APPROVED') THEN 1
            ELSE NULL
            END
        ) VIRTUAL,
    `created_at`       DATETIME              NOT NULL,
    `modified_at`      DATETIME              NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_work_report_project_id`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),

    UNIQUE KEY `uk_work_report_active_daily`
        (`work_date`, `report_user_id`, `project_id`, `active_flag`)
);


CREATE TABLE `work_report_straight`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `work_report_id`      BIGINT                NOT NULL,
    `project_straight_id` BIGINT                NOT NULL,
    `production_quantity` BIGINT                NOT NULL,
    `snapshot_serial`     VARCHAR(50)           NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `work_report_straight_serial`
(
    `id`                         BIGINT AUTO_INCREMENT NOT NULL,
    `work_report_straight_id`    BIGINT                NOT NULL,
    `project_straight_serial_id` BIGINT                NOT NULL,
    `project_straight_serial`    VARCHAR(50)           NOT NULL,
    `created_at`                 DATETIME              NOT NULL,
    `modified_at`                DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_work_report_straight_serial_work_report_straight_id`
        FOREIGN KEY (`work_report_straight_id`) REFERENCES `work_report_straight` (`id`)
);

CREATE TABLE `work_report_branch`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `work_report_id`      BIGINT                NOT NULL,
    `project_branch_id`   BIGINT                NOT NULL,
    `production_quantity` BIGINT                NOT NULL,
    `snapshot_serial`     VARCHAR(50)           NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `work_report_branch_serial`
(
    `id`                       BIGINT AUTO_INCREMENT NOT NULL,
    `work_report_branch_id`    BIGINT                NOT NULL,
    `project_branch_serial_id` BIGINT                NOT NULL,
    `project_branch_serial`    VARCHAR(50)           NOT NULL,
    `created_at`               DATETIME              NOT NULL,
    `modified_at`              DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_work_report_branch_serial_work_report_straight_id`
        FOREIGN KEY (`work_report_branch_id`) REFERENCES `work_report_branch` (`id`)
);

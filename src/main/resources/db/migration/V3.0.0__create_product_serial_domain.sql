/**
 * FileName    : V3.0.0__create_product_serial_domain.sql
 * Author      : Baekgwa
 * Date        : 2025-12-23
 * Description : 프로젝트에 할당된 직선레일과 분기레일에 제품별 관리를 위한 serial 관리 table 생성
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-12-23     Baekgwa               Initial creation
 */

CREATE TABLE `project_straight_serial`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `serial`              VARCHAR(255)          NOT NULL COMMENT '시리얼 번호',
    `project_straight_id` BIGINT                NOT NULL,
    `state`               VARCHAR(50)           NOT NULL,
    `production_state`    VARCHAR(50)           NOT NULL,
    `produced_at`         DATE                  NULL,
    `reason`              VARCHAR(50)           NULL,
    `sequence`            BIGINT                NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_straight_serial_project_straight_id`
        FOREIGN KEY (`project_straight_id`) REFERENCES `project_straight` (`id`) ON DELETE CASCADE
);

CREATE TABLE `project_branch_serial`
(
    `id`                BIGINT AUTO_INCREMENT NOT NULL,
    `serial`            VARCHAR(255)          NOT NULL COMMENT '시리얼 번호',
    `project_branch_id` BIGINT                NOT NULL,
    `state`             VARCHAR(50)           NOT NULL,
    `production_state`  VARCHAR(50)           NOT NULL,
    `produced_at`       DATE                  NULL,
    `reason`            VARCHAR(50)           NULL,
    `sequence`          BIGINT                NOT NULL,
    `created_at`        DATETIME              NOT NULL,
    `modified_at`       DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_branch_serial_project_straight_id`
        FOREIGN KEY (`project_branch_id`) REFERENCES `project_branch` (`id`) ON DELETE CASCADE
);
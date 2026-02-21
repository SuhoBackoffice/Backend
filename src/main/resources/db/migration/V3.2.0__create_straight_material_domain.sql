/**
 * FileName    : V3.2.0__create_straight_material_domain.sql
 * Author      : Baekgwa
 * Date        : 2026-01-07
 * Description : 직선 레일 BOM 등록
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2026-02-21     Baekgwa               Initial creation
**/

CREATE TABLE `project_straight_bom`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `project_straight_id` BIGINT                NOT NULL,
    `material_code`       VARCHAR(255)          NOT NULL,
    `item_name`           VARCHAR(255)          NOT NULL,
    `unit_quantity`       BIGINT                NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_straight_bom_project_straight_id`
        FOREIGN KEY (`project_straight_id`) REFERENCES `project_straight` (`id`)
);

CREATE TABLE `straight_bom_standard`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `version_id`          BIGINT                NOT NULL,
    `condition_type`      VARCHAR(50)           NOT NULL,
    `min_condition_value` DECIMAL(5, 1)         NOT NULL,
    `max_condition_value` DECIMAL(5, 1)         NOT NULL,
    `material_code`       VARCHAR(255)          NOT NULL,
    `item_name`           VARCHAR(255)          NOT NULL,
    `quantity`            BIGINT                NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_straight_bom_standard_version`
        FOREIGN KEY (`version_id`) REFERENCES `version_info` (`id`)
);

CREATE TABLE `project_straight_bom_rule`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `project_id`          BIGINT                NOT NULL,
    `condition_type`      VARCHAR(50)           NOT NULL,
    `min_condition_value` DECIMAL(5, 1)         NOT NULL,
    `max_condition_value` DECIMAL(5, 1)         NOT NULL,
    `material_code`       VARCHAR(255)          NOT NULL,
    `item_name`           VARCHAR(255)          NOT NULL,
    `quantity`            BIGINT                NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_straight_bom_rule_project`
        FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
);
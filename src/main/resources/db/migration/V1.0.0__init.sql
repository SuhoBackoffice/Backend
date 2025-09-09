/**
 * FileName    : V1.0.0__init.sql
 * Author      : Baekgwa
 * Date        : 2025-08-01
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */

CREATE TABLE `users`
(
    `id`          BIGINT       NOT NULL,
    `login_id`    VARCHAR(255) NOT NULL,
    `password`    VARCHAR(255) NOT NULL,
    `username`    VARCHAR(255) NOT NULL,
    `role`        VARCHAR(50)  NOT NULL,
    `created_at`  DATETIME     NOT NULL,
    `modified_at` DATETIME     NOT NULL,
    PRIMARY KEY `pk_user_id` (`id`)
);

CREATE TABLE `version_info`
(
    `id`             BIGINT AUTO_INCREMENT NOT NULL,
    `name`           VARCHAR(255)          NOT NULL,
    `loop_litz_wire` DECIMAL(4, 1)      NOT NULL,
    `created_at`     DATETIME              NOT NULL,
    `modified_at`    DATETIME              NOT NULL,
    UNIQUE KEY uq_version_info_name (name),
    PRIMARY KEY `pk_version_info_id` (`id`),
    CONSTRAINT `ck_version_info_loop_litz_wire_over_150` CHECK (`loop_litz_wire` >= 150.0)
);

CREATE TABLE `branch_type`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL,
    `version_id`  BIGINT                NOT NULL,
    `code`        VARCHAR(255)          NOT NULL,
    `name`        VARCHAR(255)          NOT NULL,
    `version`     DATE                  NOT NULL,
    `created_at`  DATETIME              NOT NULL,
    `modified_at` DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_branch_type_version_id` (`version_id`),
    CONSTRAINT `fk_branch_type_version_info` FOREIGN KEY (`version_id`) REFERENCES `version_info` (`id`),
    UNIQUE KEY `uk_branch_type_version_code_date` (`version_id`, `code`, `version`)
);

CREATE TABLE `branch_bom`
(
    `id`                BIGINT AUTO_INCREMENT NOT NULL,
    `branch_type_id`    BIGINT                NOT NULL,
    `item_type`         VARCHAR(100)          NOT NULL COMMENT '품목 구분 / 사출, 가공, 구매 등',
    `drawing_number`    VARCHAR(100)          NOT NULL COMMENT '도번',
    `item_name`         VARCHAR(255)          NOT NULL COMMENT '품명',
    `specification`     VARCHAR(255) COMMENT '규격',
    `unit_quantity`     BIGINT                NOT NULL COMMENT '단위 수량',
    `unit`              VARCHAR(30) COMMENT '단위 / EA, BOX, ROLL',
    `supplied_material` TINYINT(1)            NOT NULL COMMENT '사급 자재 유무',
    `created_at`        DATETIME              NOT NULL,
    `modified_at`       DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_branch_bom_branch_type_id` (`branch_type_id`),
    CONSTRAINT `fk_branch_bom_branch_type` FOREIGN KEY (`branch_type_id`) REFERENCES `branch_type` (`id`) ON DELETE CASCADE
);

CREATE TABLE `project`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL,
    `version_id`  BIGINT                NOT NULL,
    `region`      VARCHAR(255)          NOT NULL,
    `name`        VARCHAR(255)          NOT NULL,
    `start_date`  DATE,
    `end_date`    DATE,
    `created_at`  DATETIME              NOT NULL,
    `modified_at` DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_project_version_id` (`version_id`),
    CONSTRAINT `fk_project_version_info` FOREIGN KEY (`version_id`) REFERENCES `version_info` (`id`)
);

CREATE TABLE `project_branch`
(
    `id`                 BIGINT AUTO_INCREMENT NOT NULL,
    `project_id`         BIGINT                NOT NULL,
    `branch_type_id`     BIGINT                NOT NULL,
    `total_quantity`    BIGINT                NOT NULL,
    `completed_quantity` BIGINT                NOT NULL,
    `created_at`         DATETIME              NOT NULL,
    `modified_at`        DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_project_branch_project_id` (`project_id`),
    INDEX `idx_project_branch_branch_type_id` (`branch_type_id`),
    CONSTRAINT `fk_project_branch_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
    CONSTRAINT `fk_project_branch_branch_type` FOREIGN KEY (`branch_type_id`) REFERENCES `branch_type` (`id`),
    CONSTRAINT `ck_project_branch_total_quantity_nonnegative` CHECK (`total_quantity` >= 0),
    CONSTRAINT `ck_project_branch_completed_quantity_nonnegative` CHECK (`completed_quantity` >= 0)
);

CREATE TABLE `straight_type`
(
    `id`           BIGINT AUTO_INCREMENT NOT NULL,
    `type`         VARCHAR(255)          NOT NULL,
    `is_loop_rail` TINYINT(1)            NOT NULL,
    `created_at`   DATETIME              NOT NULL,
    `modified_at`  DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_straight_type_type` (`type`)
);

CREATE TABLE `project_straight`
(
    `id`                 BIGINT AUTO_INCREMENT NOT NULL,
    `project_id`         BIGINT                NOT NULL,
    `straight_type_id`   BIGINT                NOT NULL,
    `total_quantity`     BIGINT                NOT NULL,
    `completed_quantity` BIGINT                NOT NULL,
    `is_loop_rail`       TINYINT(1)            NOT NULL,
    `length`             BIGINT                NOT NULL,
    `created_at`         DATETIME              NOT NULL,
    `modified_at`        DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_project_straight_project_id` (`project_id`),
    INDEX `idx_project_straight_straight_type_id` (`straight_type_id`),
    CONSTRAINT `fk_project_straight_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
    CONSTRAINT `fk_project_straight_straight_type` FOREIGN KEY (`straight_type_id`) REFERENCES `straight_type` (`id`),
    CONSTRAINT `ck_project_straight_total_quantity_nonnegative` CHECK (`total_quantity` >= 0),
    CONSTRAINT `ck_project_straight_completed_quantity_nonnegative` CHECK (`completed_quantity` >= 0),
    CONSTRAINT `ck_project_straight_length_over_300` CHECK (`length` >= 300),
    UNIQUE KEY `uk_project_straight_project_id_straight_type_id_length` (`project_id`, `straight_type_id`, `length`)
);

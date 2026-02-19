/**
 * FileName    : V2.0.0__create_material_inbound.sql
 * Author      : Baekgwa
 * Date        : 2025-09-19
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-19     Baekgwa               Initial creation
 */

CREATE TABLE `material_history`
(
    `id`            BIGINT AUTO_INCREMENT NOT NULL,
    `project_id`    BIGINT                NOT NULL,
    `material_code` VARCHAR(255)          NOT NULL COMMENT '도번 혹은 식별자',
    `item_name`     VARCHAR(255)          NOT NULL COMMENT '품명',
    `quantity`      BIGINT                NOT NULL COMMENT '변동 수량',
    `description`   VARCHAR(255)          NOT NULL COMMENT '변동에 대한 설명',
    `type`          VARCHAR(50)           NOT NULL,
    `created_at`    DATETIME              NOT NULL,
    `modified_at`   DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_material_history_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
);

CREATE TABLE `project_material_stock`
(
    `id`                     BIGINT AUTO_INCREMENT NOT NULL,
    `project_id`             BIGINT                NOT NULL,
    `material_code`          VARCHAR(255)          NOT NULL COMMENT '도번 혹은 식별자',
    `item_name`              VARCHAR(255)          NOT NULL COMMENT '품명',
    `total_plan_quantity`    BIGINT                NOT NULL,
    `total_inbound_quantity` BIGINT                NOT NULL,
    `total_used_quantity`    BIGINT                NOT NULL,
    `created_at`             DATETIME              NOT NULL,
    `modified_at`            DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_material_stock_project_drawing` (`project_id`, `material_code`),
    CONSTRAINT `fk_project_material_stock_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
);

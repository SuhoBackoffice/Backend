/**
 * FileName    : V3.0.1__create_product_history_table.sql
 * Author      : Baekgwa
 * Date        : 2025-12-26
 * Description : 프로젝트에 할당된 직선레일과 분기레일에 제품별 관리를 위한 serial 관리 table 생성
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-12-26     Baekgwa               Initial creation
 */

CREATE TABLE `project_straight_history`
(
    `id`                  BIGINT AUTO_INCREMENT NOT NULL,
    `change_user_id`      BIGINT                NOT NULL,
    `change_user_name`    VARCHAR(50)           NOT NULL,
    `project_id`          BIGINT                NOT NULL,
    `project_straight_id` BIGINT                NOT NULL,
    `straight_serial`     VARCHAR(50)           NOT NULL,
    `action`              VARCHAR(50)           NOT NULL,
    `before_quantity`     BIGINT                NOT NULL,
    `after_quantity`      BIGINT                NOT NULL,
    `created_at`          DATETIME              NOT NULL,
    `modified_at`         DATETIME              NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `project_branch_history`
(
    `id`                BIGINT AUTO_INCREMENT NOT NULL,
    `change_user_id`    BIGINT                NOT NULL,
    `project_id`        BIGINT                NOT NULL,
    `project_branch_id` BIGINT                NOT NULL,
    `branch_serial`     VARCHAR(50)           NOT NULL,
    `action`            VARCHAR(50)           NOT NULL,
    `before_quantity`   BIGINT                NOT NULL,
    `after_quantity`    BIGINT                NOT NULL,
    `created_at`        DATETIME              NOT NULL,
    `modified_at`       DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_branch_history_user` FOREIGN KEY (`change_user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_project_branch_history_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
    CONSTRAINT `fk_project_branch_history_project_branch` FOREIGN KEY (`project_branch_id`) REFERENCES `project_branch` (`id`)
);
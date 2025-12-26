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
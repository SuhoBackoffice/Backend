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

CREATE TABLE `material_inbound`
(
    `id`             BIGINT       NOT NULL,
    `project_id`     BIGINT       NOT NULL,
    `drawing_number` VARCHAR(255) NOT NULL COMMENT '도번',
    `item_name`      VARCHAR(255) NOT NULL COMMENT '품명',
    `quantity`       BIGINT       NOT NULL COMMENT '입고 수량',
    `created_at`     DATETIME     NOT NULL,
    `modified_at`    DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_material_inbound_project_id` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
)
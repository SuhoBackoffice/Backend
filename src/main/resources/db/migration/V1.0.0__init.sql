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
)
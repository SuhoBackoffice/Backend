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
    `id`          BIGINT AUTO_INCREMENT NOT NULL,
    `name`        VARCHAR(255)          NOT NULL,
    `created_at`  DATETIME              NOT NULL,
    `modified_at` DATETIME              NOT NULL,
    UNIQUE KEY uq_version_info_name (name),
    PRIMARY KEY `pk_version_info_id` (`id`)
);
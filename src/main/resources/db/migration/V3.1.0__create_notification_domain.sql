/**
 * FileName    : V3.1.0__create_notification_domain.sql
 * Author      : Baekgwa
 * Date        : 2026-01-07
 * Description : 알림 생성을 위한 도메인
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-12-29     Baekgwa               Initial creation
 */

CREATE TABLE `notification`
(
    `id`          BIGINT AUTO_INCREMENT NOT NULL,
    `content`     VARCHAR(50)           NOT NULL,
    `url`         VARCHAR(100)          NULL,
    `type`        VARCHAR(50)           NOT NULL,
    `created_at`  DATETIME              NOT NULL,
    `modified_at` DATETIME              NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `user_notification`
(
    `id`              BIGINT AUTO_INCREMENT NOT NULL,
    `user_id`         BIGINT                NOT NULL,
    `notification_id` BIGINT                NOT NULL,
    `is_read`         BOOLEAN               NOT NULL,
    `read_at`         DATETIME              NULL,
    `created_at`      DATETIME              NOT NULL,
    `modified_at`     DATETIME              NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_user_notification_user_id`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_user_notification_notification_id`
        FOREIGN KEY (`notification_id`)
            REFERENCES `notification` (`id`)
            ON DELETE CASCADE
);
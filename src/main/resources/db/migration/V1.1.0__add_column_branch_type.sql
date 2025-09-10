/**
 * FileName    : V1.1.0__add_column_branch_type.sql
 * Author      : Baekgwa
 * Date        : 2025-09-10
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */

ALTER TABLE `branch_type`
    ADD COLUMN `image_url` VARCHAR(255) NULL;

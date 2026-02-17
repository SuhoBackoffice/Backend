/**
 * FileName    : V3.2.0__merge_straight_info.sql
 * Author      : Baekgwa
 * Date        : 2026-02-17
 * Description :
 * =====================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-12-29     Baekgwa               Initial creation
 */

ALTER TABLE project_straight
    DROP FOREIGN KEY fk_project_straight_straight_info;

ALTER TABLE project_straight
    DROP COLUMN straight_info_id;

ALTER TABLE project_straight
    ADD COLUMN hole_position DECIMAL(5,1) NULL,
    ADD COLUMN litzwire1 DECIMAL(5,1) NULL,
    ADD COLUMN litzwire2 DECIMAL(5,1) NULL,
    ADD COLUMN litzwire3 DECIMAL(5,1) NULL,
    ADD COLUMN litzwire4 DECIMAL(5,1) NULL,
    ADD COLUMN litzwire5 DECIMAL(5,1) NULL,
    ADD COLUMN litzwire6 DECIMAL(5,1) NULL;

DROP TABLE straight_info;

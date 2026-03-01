
CREATE DATABASE `file-type` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `file-type`;
create table if not exists files
(
    id                bigint auto_increment
    primary key,
    file_name         varchar(255) not null,
    file_size         bigint       not null,
    upload_time       datetime     not null,
    recognition_time  double       null,
    predicted_type    varchar(50)  null,
    actual_type       varchar(50)  null,
    comparison_result tinyint(1)   null,
    file_content      longblob     null
    )
    collate = utf8mb4_unicode_ci;


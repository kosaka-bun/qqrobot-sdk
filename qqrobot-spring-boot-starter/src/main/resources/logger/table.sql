create table if not exists `usage_log`
(
    `id` int primary key auto_increment,
    `datetime` timestamp,
    `qq` bigint,
    `groupName` varchar,
    `username` varchar,
    `msg` varchar,
    `reply` varchar
);

create table if not exists `exception_record`
(
    `id` int primary key auto_increment,
    `datetime` timestamp,
    `exceptionText` varchar
);

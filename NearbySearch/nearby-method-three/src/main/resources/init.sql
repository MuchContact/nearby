CREATE TABLE `nearby`.`user_geohash` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`latitude` double DEFAULT NULL,
	`longitude` double DEFAULT NULL,
	`name` varchar(255) DEFAULT NULL,
  `geo_code` varchar(255) DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`is_array` tinyint(4) DEFAULT 0,
	PRIMARY KEY (`id`)
) ENGINE=`InnoDB` AUTO_INCREMENT=1 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ROW_FORMAT=COMPACT COMMENT='' CHECKSUM=0 DELAY_KEY_WRITE=0;
CREATE TABLE t_article (
	id int8 NOT NULL,
	create_time TIMESTAMP,
	creator int8,
	is_delete VARCHAR ( 2 ),
	update_time TIMESTAMP,
	updater int8,
	cover_url VARCHAR ( 255 ),
	html_content TEXT,
	introduction VARCHAR ( 255 ),
	md_content TEXT,
	tags VARCHAR ( 255 ),
	title VARCHAR ( 255 ),
	weight INT4,
	category_id int8,
	PRIMARY KEY ( id )
);
CREATE TABLE t_category (
	id int8 NOT NULL,
	create_time TIMESTAMP,
	creator int8,
	is_delete VARCHAR ( 2 ),
	update_time TIMESTAMP,
	updater int8,
	name VARCHAR ( 255 ),
	PRIMARY KEY ( id )
);
CREATE TABLE t_sys_permission (
	id int8 NOT NULL,
	create_time TIMESTAMP,
	creator int8,
	is_delete VARCHAR ( 2 ),
	update_time TIMESTAMP,
	updater int8,
	icon VARCHAR ( 50 ),
	name VARCHAR ( 255 ),
	order_number int4,
	parent_id int8,
	type int4,
	url VARCHAR ( 255 ),
	val VARCHAR ( 255 ),
	PRIMARY KEY ( id )
);
CREATE TABLE t_sys_role (
	id int8 NOT NULL,
	create_time TIMESTAMP,
	creator int8,
	is_delete VARCHAR ( 2 ),
	update_time TIMESTAMP,
	updater int8,
	comments VARCHAR ( 1024 ),
	name VARCHAR ( 255 ),
	PRIMARY KEY ( id )
);
CREATE TABLE t_sys_role_permission ( role_id int8 NOT NULL, permission_id int8 NOT NULL );
CREATE TABLE t_sys_user (
	create_time TIMESTAMP,
	creator int8,
	is_delete VARCHAR ( 2 ),
	update_time TIMESTAMP,
	updater int8,
	nick_name VARCHAR ( 255 ),
	password VARCHAR ( 255 ),
	phone VARCHAR ( 11 ),
	sex int4,
	username VARCHAR ( 255 ),
	PRIMARY KEY ( username )
);
CREATE TABLE t_sys_user_role ( user_id int8 NOT NULL, role_id int8 NOT NULL );
CREATE SEQUENCE seq_article START 1 INCREMENT 1;
CREATE SEQUENCE seq_category START 1 INCREMENT 1;
CREATE SEQUENCE seq_sys_permission START 1 INCREMENT 1;
CREATE SEQUENCE seq_sys_role START 1 INCREMENT 1;
CREATE SEQUENCE seq_sys_user START 1 INCREMENT 1;
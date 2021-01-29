CREATE DATABASE maildb;

use maildb;

CREATE TABLE user_info(
    user_id INT(10) NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(30) NOT NULL,
    password VARCHAR(30) NOT NULL,
    pop_server VARCHAR(30) NOT NULL,
    pop_port INT(10) NOT NULL,
    smtp_server VARCHAR(30) NOT NULL,
    smtp_port INT(10) NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE mail_t(
    mail_id INT(10) NOT NULL AUTO_INCREMENT,
    receiver_name VARCHAR(30) NOT NULL,
    subject VARCHAR(50),
    sender_name VARCHAR(30),
    content MEDIUMTEXT,
    receive_date TIMESTAMP,
    send_date TIMESTAMP,
    PRIMARY KEY(mail_id)
);

CREATE TABLE attachment_t(
    mail_id INT(10) NOT NULL,
    attachment_path VARCHAR(50) NOT NULL
);

INSERT INTO user_info SET 
user_name = 'yangchen323@sina.com',
password = '4f6c9ead7a16ba31',
pop_server = 'pop.sina.com',
pop_port = 995,
smtp_server = 'smtp.sina.com',
smtp_port = 465;
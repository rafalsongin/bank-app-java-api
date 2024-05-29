INSERT INTO `user` ( `username`, `email`, `password`, `isEmployee`, `JWT`,`first_name`,`last_name`,`bank_id`)
VALUES ('test', 'test@test', 'test', '1', 'test', 'Mariia', 'Kovalenko', 1);

INSERT INTO `bank` (`name`, `currency`)
VALUES ( 'Bank of Ukraine', 'UAH');

select * from user;

update account
set account_status = '1';
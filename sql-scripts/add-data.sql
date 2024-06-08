INSERT INTO `user` ( `username`, `email`, `password`, `isEmployee`, `JWT`,`first_name`,`last_name`,`bank_id`)
VALUES ('test', 'test@test', 'test', '1', 'test', 'Mariia', 'Kovalenko', 1);

INSERT INTO `bank` (`name`, `currency`)
VALUES ( 'Bank of Ukraine', 'UAH');

select * from user;

update account
set account_approval_status = '1';

INSERT INTO `transaction` (transaction_type, amount, timestamp, from_account, to_account, initiated_by_user)
VALUES
    ('Internal Transaction', 500.00, '2023-05-20 14:30:00', 49, 45, 29),
    ('Internal Transaction', 200.00, '2023-05-21 10:15:00', 49, 45, 29),
    ('Internal Transaction', 300.00, '2023-05-22 09:45:00', 49, 45, 29),
    ('Internal Transaction', 100.00, '2023-05-23 12:00:00', 45, 49, 30),
    ('Internal Transaction', 450.00, '2023-05-24 16:20:00', 45, 49, 30);

INSERT INTO `transaction` (transaction_type, amount, timestamp, from_account, to_account, initiated_by_user)
VALUES
    ('Internal Transaction', 200.00, '2024-06-04 14:30:00', 49, 50, 29);
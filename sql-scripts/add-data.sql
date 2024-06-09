
INSERT INTO `user` ( `username`, `email`, `password`, `user_role`, `JWT`,`first_name`,`last_name`,`bank_id`)
VALUES ('test', 'test@test', 'test', '1', 'test', 'Mariia', 'Kovalenko', 1);

update customer
set account_approval_status = 'UNVERIFIED';

INSERT INTO `transaction` (transaction_type, amount, timestamp, from_account, to_account, initiated_by_user)
VALUES
    ('External Transaction', 500.00, '2024-05-30 14:30:00', 49, 45, 29);

INSERT INTO `transaction` (transaction_type, amount, timestamp, from_account, to_account, initiated_by_user)
VALUES
    ('Internal Transaction', 200.00, '2024-06-04 14:30:00', 49, 50, 29);

delete from account
where account_id in (75, 76, 62);

delete from account
where customer_id = 30;

update customer
set account_approval_status = 'UNVERIFIED'
where user_id = 30;


SELECT customer_id
FROM account
GROUP BY customer_id
HAVING COUNT(account_id) = 2;

UPDATE customer
SET account_approval_status = 'Verified'
WHERE user_id IN (
    SELECT customer_id
    FROM account
    GROUP BY customer_id
    HAVING COUNT(account_id) = 2
);
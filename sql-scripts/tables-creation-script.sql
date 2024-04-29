CREATE TABLE `User` (
                        `user_id` INT AUTO_INCREMENT,
                        `username` VARCHAR(255) NOT NULL UNIQUE,
                        `email` VARCHAR(255) NOT NULL,
                        `password` VARCHAR(255) NOT NULL,
                        `isEmployee` BOOLEAN NOT NULL,
                        `JWT` TEXT,
                        `first_name` VARCHAR(255) NOT NULL,
                        `last_name` VARCHAR(255) NOT NULL,
                        `bank_id` INT NOT NULL,
                        FOREIGN KEY (`bank_id`) REFERENCES `Bank` (`bank_id`),
                        PRIMARY KEY (`user_id`)
);

CREATE TABLE `Customer` (
                            `customer_id` INT AUTO_INCREMENT,
                            `user_id` INT NOT NULL,
                            `BSN_number` VARCHAR(50) NOT NULL,
                            `phone_number` VARCHAR(50),
                            `account_approval_status` VARCHAR(50),
                            `transaction_limits` FLOAT,
                            PRIMARY KEY (`customer_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`)
);

CREATE TABLE `Employee` (
                            `employee_id` INT AUTO_INCREMENT,
                            `user_id` INT NOT NULL,
                            `employee_role` VARCHAR(50) NOT NULL,
                            PRIMARY KEY (`employee_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`)
);

CREATE TABLE `Bank` (
                        `bank_id` INT AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL,
                        `currency` VARCHAR(3) NOT NULL,
                        PRIMARY KEY (`bank_id`)
);

CREATE TABLE `Account` (
                           `account_id` INT AUTO_INCREMENT,
                            `customer_id` INT NOT NULL,
                           `IBAN` VARCHAR(34) NOT NULL UNIQUE,
                           `account_type` VARCHAR(50) NOT NULL,
                           `balance` FLOAT NOT NULL,
                           `absolute_transfer_limit` FLOAT NOT NULL,
                           `daily_transfer_limit` FLOAT NOT NULL,
                           PRIMARY KEY (`account_id`),
                            FOREIGN KEY (`customer_id`) REFERENCES `Customer` (`customer_id`)
);

CREATE TABLE `Transaction` (
                               `transaction_id` INT AUTO_INCREMENT,
                                `transaction_type` VARCHAR(50) NOT NULL,
                               `amount` FLOAT NOT NULL,
                               `timestamp` DATETIME NOT NULL,
                               `from_account` INT NOT NULL,
                               `to_account` INT NOT NULL,
                               `initiated_by_user` INT NOT NULL,
                               PRIMARY KEY (`transaction_id`),
                               FOREIGN KEY (`from_account`) REFERENCES `Account` (`account_id`),
                               FOREIGN KEY (`to_account`) REFERENCES `Account` (`account_id`),
                               FOREIGN KEY (`initiated_by_user`) REFERENCES `User` (`user_id`)
);

DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS `Customer`;
DROP TABLE IF EXISTS `Employee`;
DROP TABLE IF EXISTS `Bank`;
DROP TABLE IF EXISTS `Account`;
DROP TABLE IF EXISTS `Transaction`;

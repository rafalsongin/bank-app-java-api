package com.inholland.bankapp.repository;
import com.inholland.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}

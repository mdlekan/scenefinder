package com.mikelekan.scenefinder.repository;

import com.mikelekan.scenefinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>
{

}

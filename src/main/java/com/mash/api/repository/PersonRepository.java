package com.mash.api.repository;

import com.mash.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer>{

    Person findByAccountId(Integer accountId);
}

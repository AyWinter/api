package com.mash.api.service;

import com.mash.api.entity.Person;

public interface PersonService {

    Person add(Person person);

    Person findById(Integer id);

    Person findByAccountId(Integer accountId);
}

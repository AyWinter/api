package com.mash.api.service.impl;

import com.mash.api.entity.Person;
import com.mash.api.repository.PersonRepository;
import com.mash.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Person add(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person findById(Integer id) {
        return personRepository.findOne(id);
    }

    @Override
    public Person findByAccountId(Integer accountId) {
        return personRepository.findByAccountId(accountId);
    }
}

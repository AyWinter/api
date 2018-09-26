package com.mash.api.service.impl;

import com.mash.api.entity.Parameter;
import com.mash.api.repository.ParameterRepository;
import com.mash.api.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParameterServiceImpl implements ParameterService{

    @Autowired
    private ParameterRepository parameterRepository;

    @Override
    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Override
    public List<Parameter> findByName(String name) {
        return parameterRepository.findByName(name);
    }

    @Override
    public List<Parameter> findAll() {
        return parameterRepository.findAll();
    }
}

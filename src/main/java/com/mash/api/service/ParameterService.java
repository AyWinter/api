package com.mash.api.service;

import com.mash.api.entity.Parameter;

import java.util.List;

public interface ParameterService {

    Parameter save(Parameter parameter);

    List<Parameter> findByName(String name);

    List<Parameter> findAll();
}

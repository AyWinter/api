package com.mash.api.repository;

import com.mash.api.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParameterRepository extends JpaRepository<Parameter, Integer>{

    List<Parameter> findByName(String name);
}

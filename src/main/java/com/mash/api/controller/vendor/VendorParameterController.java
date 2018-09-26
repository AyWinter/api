package com.mash.api.controller.vendor;

import com.mash.api.entity.Parameter;
import com.mash.api.entity.ParameterResult;
import com.mash.api.entity.Result;
import com.mash.api.service.ParameterService;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class VendorParameterController {

    @Autowired
    private ParameterService parameterService;

    @GetMapping(value="/vendor/parameter")
    public Result<ParameterResult> find()
    {
        List<Parameter> parameters = parameterService.findAll();

        List<Parameter> productType = new ArrayList<Parameter>();
        List<Parameter> productLevel = new ArrayList<Parameter>();
        List<Parameter> material = new ArrayList<Parameter>();
        List<Parameter> lighting = new ArrayList<Parameter>();
        List<Parameter> visitorsFlowrate = new ArrayList<Parameter>();
        List<Parameter> vehicleFlowrate = new ArrayList<Parameter>();
        List<Parameter> useType = new ArrayList<Parameter>();

        for (Parameter p : parameters)
        {
            if (p.getName().equals("product_type"))
            {
                productType.add(p);
            }
            if (p.getName().equals("product_level"))
            {
                productLevel.add(p);
            }
            if (p.getName().equals("material"))
            {
                material.add(p);
            }
            if (p.getName().equals("lighting"))
            {
                lighting.add(p);
            }
            if (p.getName().equals("visitors_flowrate"))
            {
                visitorsFlowrate.add(p);
            }
            if (p.getName().equals("vehicle_flowrate"))
            {
                vehicleFlowrate.add(p);
            }
            if (p.getName().equals("use_type"))
            {
                useType.add(p);
            }
        }

        ParameterResult parameterResult = new ParameterResult();
        parameterResult.setProductType(productType);
        parameterResult.setProductLevel(productLevel);
        parameterResult.setMaterial(material);
        parameterResult.setLighting(lighting);
        parameterResult.setVisitorsFlowrate(visitorsFlowrate);
        parameterResult.setVehicleFlowrate(vehicleFlowrate);
        parameterResult.setUseType(useType);
        return ResultUtil.success(parameterResult);
    }

    public List<Parameter> findByName(String name)
    {
        return parameterService.findByName(name);
    }
}

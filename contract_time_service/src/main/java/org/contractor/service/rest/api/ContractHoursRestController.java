package org.contractor.service.rest.api;


import org.contractor.core.data.ResultDataSet;
import org.contractor.service.coreservices.impl.ContractServiceWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.contractor.service.rest.api.ContractHoursRestMappings;

@RestController
public class ContractHoursRestController {
    @Autowired
    private ContractServiceWeb service;

    @RequestMapping(value = ContractHoursRestMappings.DEFUALT, method = RequestMethod.GET)
    public ResultDataSet
    defaultHolidaysCalc() {
        return service.defaultAllHolidaysCalc();
    }

    @RequestMapping(value = ContractHoursRestMappings.NEWDAYSCALC, method = RequestMethod.POST)
    public String newDaysCalc(String name, String desc) {
        return "ok";
    }
}

package org.contractor.service.coreservices.impl;

import org.contractor.core.data.ResultDataSet;
import org.contractor.core.service.impl.CalculateDateServiceImpl;
import org.springframework.stereotype.Service;

@Service("contractServiceWeb")
public class ContractServiceWeb extends CalculateDateServiceImpl {

    @Override
    public ResultDataSet defaultAllHolidaysCalc() {
        return super.defaultAllHolidaysCalc();
    }
}

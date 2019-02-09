package service;

import org.contractor.core.data.ResultDataSet;
import org.contractor.core.service.impl.CalculateDateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CalculateDateServiceImpl.class)
class CalculateDateServiceTest {
    @Autowired
    CalculateDateServiceImpl calculateDateService;
    @Test
    void buildTable() {
        ResultDataSet dataSet = calculateDateService.defaultAllHolidaysCalc();
        System.out.println("The final data set is " + dataSet.toString());
    }
}
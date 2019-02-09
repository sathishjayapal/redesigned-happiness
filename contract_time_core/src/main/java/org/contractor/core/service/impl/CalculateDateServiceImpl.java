package org.contractor.core.service.impl;

import io.lamma.Date;
import io.lamma.HolidayRule;
import lombok.Getter;
import lombok.Setter;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.contractor.core.data.AuditedObject;
import org.contractor.core.data.ITContractTable;
import org.contractor.core.data.ResultDataSet;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.contractor.core.service.impl.IContractDateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.lamma.Dates.from;
import static io.lamma.HolidayRules.simpleRule;
import static io.lamma.HolidayRules.weekends;

@Service("calculateDateService")
public class CalculateDateServiceImpl implements IContractDateService {

    @Getter
    @Setter
    public BigDecimal OLD_RATE = new BigDecimal(74.21);
    @Getter
    @Setter
    public BigDecimal REVISED_RATE = new BigDecimal(79.19);

    public static final int springNumberOfDays = 5;
    public static final int regularDaysOff = 5;
    public static final int winterNumberOfDays = 5;
    public static final int summerNumberOfDays = 5;

    public static final int DAILY_WORK_HOUR = 8;

    public static final String PLANNED_HOURS = "Planned hours";
    public static final String ACTUAL_HOURS = "Actual hours";
    public static final String NEWLINE = "\n";
    public static final String DESCRIPTION = "Description";

    public static final String PAID = "Paid";

    public static final String HOURS_TABLE_HEADER = "Hours data";
    public static final String HOURS = "Hours";
    public static final String NEW_BILLING_HEADER = "Revised payment";
    public static final String OLD_BILLING_HEADER = "Actual payment";

    public static final String PLANNED_MONEY = "Planned money";
    public static final String ACTUAL_MONEY = "Actual money";
    public static final String DIFF = "Difference money";

    int CONTRACT_FINISH_YEAR = 2019;
    int CONTRACT_START_YEAR = 2018;
    int CONTRACT_START_MONTH = 7;
    int CONTRACT_START_DAY = 1;
    int CONTRACT_END_MONTH = 6;
    int CONTRACT_END_DATE = 30;


    public final HolidayRule usHolidays = simpleRule
            (new Date(CONTRACT_FINISH_YEAR, 1, 1),
                    new Date(CONTRACT_FINISH_YEAR, 1, 15),
                    new Date(CONTRACT_FINISH_YEAR, 2, 19),
                    new Date(CONTRACT_FINISH_YEAR, 4, 16),
                    new Date(CONTRACT_FINISH_YEAR, 5, 28),
                    new Date(CONTRACT_FINISH_YEAR, 7, 4),
                    new Date(CONTRACT_FINISH_YEAR, 9, 3),
                    new Date(CONTRACT_FINISH_YEAR, 10, 8),
                    new Date(CONTRACT_FINISH_YEAR, 11, 12),
                    new Date(CONTRACT_FINISH_YEAR, 12, 25),
                    new Date(CONTRACT_START_YEAR, 1, 1),
                    new Date(CONTRACT_START_YEAR, 1, 15),
                    new Date(CONTRACT_START_YEAR, 2, 19),
                    new Date(CONTRACT_START_YEAR, 4, 16),
                    new Date(CONTRACT_START_YEAR, 5, 28),
                    new Date(CONTRACT_START_YEAR, 7, 4),
                    new Date(CONTRACT_START_YEAR, 9, 3),
                    new Date(CONTRACT_START_YEAR, 10, 8),
                    new Date(CONTRACT_START_YEAR, 11, 12),
                    new Date(CONTRACT_START_YEAR, 12, 25));

    HolidayRule summerHolidays = simpleRule(from(new Date(CONTRACT_START_YEAR, Month.AUGUST.getValue(), 1))
            .to(new Date(CONTRACT_START_YEAR, Month.AUGUST.getValue(), 31))
            .except(weekends()
                    .and(usHolidays))
            .byDays(summerNumberOfDays)
            .build()
            .toArray(new Date[0]));

    HolidayRule winterHolidays = simpleRule(from(new Date(CONTRACT_START_YEAR, Month.DECEMBER.getValue(), 1))
            .to(new Date(CONTRACT_START_YEAR, Month.DECEMBER.getValue(), 31))
            .except(weekends()
                    .and(usHolidays))
            .byDays(winterNumberOfDays)
            .build()
            .toArray(new Date[0]));

    HolidayRule springHolidays = simpleRule(from(new Date(CONTRACT_FINISH_YEAR, Month.MARCH.getValue(), 1))
            .to(new Date(CONTRACT_FINISH_YEAR, Month.MARCH.getValue(), 31))
            .except(weekends()
                    .and(usHolidays))
            .byDays(springNumberOfDays)
            .build()
            .toArray(new Date[0]));

    HolidayRule regularHolidays = simpleRule(from(new Date(CONTRACT_FINISH_YEAR, Month.MAY.getValue(), 1))
            .to(new Date(CONTRACT_FINISH_YEAR, Month.MAY.getValue(), 31))
            .except(weekends()
                    .and(usHolidays))
            .byDays(regularDaysOff)
            .build()
            .toArray(new Date[0]));


    /**
     * Default method calculator
     */
    public ResultDataSet defaultAllHolidaysCalc() {
        ResultDataSet returnResults = new ResultDataSet();
        AtomicInteger plannedCounter = new AtomicInteger(0);
        AtomicInteger actualCounter = new AtomicInteger(0);

        calculatePlannedData(plannedCounter);
        //Customize HolidayRule to be applied.

        HolidayRule appliedRules = weekends()
                .and(usHolidays)
                .and(summerHolidays)
                .and(winterHolidays)
                .and(springHolidays)
                .and(regularHolidays);

        List<Date> allFilteredDates = from(CONTRACT_START_YEAR, CONTRACT_START_MONTH, CONTRACT_START_DAY)
                .to(CONTRACT_FINISH_YEAR, CONTRACT_END_MONTH, CONTRACT_END_DATE)
                .except(appliedRules)
                .build();
        allFilteredDates
                .stream()
                .map(date -> {
                    actualCounter.addAndGet(DAILY_WORK_HOUR);
                    return date.dd();
                })
                .collect(Collectors.toList());

        try {
            returnResults = buildTable(plannedCounter, actualCounter);
        } catch (Exception e) {
            returnResults.addError(1000L, "System error when doing Markdown");
        }
        return returnResults;
    }

    /**
     * Calculates the amount based on actual 2000 hours of working. Includes the US Holidays.
     *
     * @param plannedCounter
     */
    private void calculatePlannedData(AtomicInteger plannedCounter) {
        List<Date> filteredDates = from(CONTRACT_START_YEAR, CONTRACT_START_MONTH, CONTRACT_START_DAY)
                .to(CONTRACT_FINISH_YEAR, CONTRACT_END_MONTH, CONTRACT_END_DATE)
                .except(weekends()
                        .and(usHolidays))
                .build();
        filteredDates
                .stream()
                .map(date -> {
                    plannedCounter.addAndGet(DAILY_WORK_HOUR);
                    return date.dd();
                })
                .collect(Collectors.toList());
    }

    /**
     * Build table composition data.
     *
     * @param plannedCounter
     * @param actualCounter
     * @throws Exception
     */
    public ResultDataSet buildTable(AtomicInteger plannedCounter, AtomicInteger actualCounter) {
        ResultDataSet resultDataSet = new ResultDataSet();
        //Heading for the Actual and planned hours information.
        StringBuilder hours = new StringBuilder()
                .append(new Heading(HOURS_TABLE_HEADER, 1))
                .append(NEWLINE);
        Set<AuditedObject> auditedObjects = new HashSet<>();
        auditedObjects.add(buildPlannedTime(plannedCounter, actualCounter, hours));

        //Heading for the New Billing information
        StringBuilder NewMoney = new StringBuilder()
                .append(new Heading(NEW_BILLING_HEADER, 1))
                .append(NEWLINE);
        auditedObjects.add(buildMoneyDetails(plannedCounter, actualCounter,
                NewMoney, PLANNED_MONEY, REVISED_RATE, ACTUAL_MONEY, DIFF));
        //Heading for the Old billing information
        StringBuilder oldMoney = new StringBuilder()
                .append(new Heading(OLD_BILLING_HEADER, 1))
                .append(NEWLINE);
        auditedObjects.add(buildMoneyDetails(plannedCounter, actualCounter,
                oldMoney, PLANNED_MONEY, OLD_RATE, ACTUAL_MONEY, DIFF));
        resultDataSet.setResultObjects(auditedObjects);
        return resultDataSet;
    }

    /**
     * Markdown table to build Money details
     *
     * @param plannedCounter
     * @param actualCounter
     * @param moneyVerbiage
     * @param descTable
     * @param money
     * @param col1Desc
     * @param col2Desc
     */
    private ITContractTable buildMoneyDetails(AtomicInteger plannedCounter,
                                              AtomicInteger actualCounter,
                                              StringBuilder moneyVerbiage,
                                              String descTable,
                                              BigDecimal money,
                                              String col1Desc,
                                              String col2Desc) {
        money.setScale(2, RoundingMode.HALF_UP);
        Table.Builder moneyTableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_RIGHT)
                .withRowLimit(4)
                .addRow(DESCRIPTION, PAID);
        BigDecimal calculatedVal = new BigDecimal(plannedCounter.get() * money.floatValue());
        calculatedVal.setScale(2, RoundingMode.HALF_UP);
        moneyTableBuilder.
                addRow(descTable, calculatedVal);
        moneyTableBuilder.addRow(col1Desc, actualCounter.get() * money.floatValue());
        moneyTableBuilder.addRow(col2Desc,
                ((plannedCounter.get() * money.floatValue()) - (actualCounter.get() * money.floatValue())));
        ITContractTable itContractTable = new ITContractTable();
        itContractTable.setContractTableHeader(moneyVerbiage.toString());
        itContractTable.setContractMoneyMarkDownTable(moneyTableBuilder.build().toString());
        itContractTable.setCreateDate(LocalDate.now().toDate());
        itContractTable.setCreateUserID("OWNER");
        return itContractTable;
    }

    /**
     * Markdown table to build Planned and Actual hours table.
     *
     * @param plannedCounter
     * @param actualCounter
     * @param hours
     */
    private ITContractTable buildPlannedTime(AtomicInteger plannedCounter,
                                             AtomicInteger actualCounter,
                                             StringBuilder hours) {
        Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_RIGHT)
                .withRowLimit(3)
                .addRow(DESCRIPTION, HOURS);
        tableBuilder.addRow(PLANNED_HOURS, plannedCounter.get());
        tableBuilder.addRow(ACTUAL_HOURS, actualCounter.get());
        ITContractTable itContractHour = new ITContractTable();
        itContractHour.setCreateDate(LocalDate.now().toDate());
        itContractHour.setCreateUserID("OWNER");
        itContractHour.setContractTableHeader(hours.toString());
        itContractHour.setContractMoneyMarkDownTable(tableBuilder.build().toString());
        return itContractHour;
    }
}

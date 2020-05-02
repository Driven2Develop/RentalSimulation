package sm.rental;

import cern.jet.stat.Probability;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

public class DisplayResult {
    /*
    Save all the different cases as instance variables. Notice the 'final' tag since they will remain unaltered.
     */
    @Getter
    private final List<List<Result>> case1;
    @Getter
    private final List<List<Result>> case2;
    @Getter
    private final List<List<Result>> case3;
    @Getter
    private final List<List<Result>> improvedCase1;
    @Getter
    private final List<List<Result>> improvedCase2;
    @Getter
    private final List<List<Result>> improvedCase3;
    @Getter
    private final int NUMRUNS;
    @Getter
    private final double confidence;

    //RAWRS WHY DO I NEEED TO FIX THIS FRINTING SHENANIGANS
    String sfinterval = "\t[%6.2f - %6.2f, %6.2f + %6.2f]";

    /*
    Constructor to initialize instance variables.
     */
    public DisplayResult(List<List<Result>> c1,
                         List<List<Result>> c2,
                         List<List<Result>> c3,
                         List<List<Result>> ic1,
                         List<List<Result>> ic2,
                         List<List<Result>> ic3,
                         int runs,
                         double confidence) {
        this.improvedCase1 = ic1;
        this.improvedCase2 = ic2;
        this.improvedCase3 = ic3;
        this.case1 = c1;
        this.case2 = c2;
        this.case3 = c3;
        this.NUMRUNS = runs;
        this.confidence = confidence;
    }

    /*
    Displays an individual case in an easy to read format that gets displayed.
    Confidence interval is also displayed using StatisticsUtils class.
     */
    public String ShowTable(List<List<Result>> showMe) {
        // array of size 3 to store the separate subcases when van capacity is equal to 12, 18, and 30.
        StatisticsUtils[] CustServedStats = new StatisticsUtils[3];
        StatisticsUtils[] SatisfactionRateStats = new StatisticsUtils[3];
        StatisticsUtils[] CostStats = new StatisticsUtils[3];
        //initialize the arrays
        for (int i = 0; i < CustServedStats.length; i++) {
            CustServedStats[i] = new StatisticsUtils();
            SatisfactionRateStats[i] = new StatisticsUtils();
            CostStats[i] = new StatisticsUtils();
        }
        //counter for the current run
        int runNumber;
        //initialize stringbuilder object
        StringBuilder s = new StringBuilder();

        //Add header at the top of table
        s.append("Run Number \t Van Capacity \t Customers Served \t Customer Satisfaction \t Cost");
        s.append("\n");

        //Iterate through the case and add its contents, with adequate spacing.
        for (List<Result> i : showMe) {
            runNumber = 1;
            for (Result j : i) {
                s.append(runNumber++);
                s.append("\t\t\t\t");
                s.append(j.getCapacity());
                s.append("\t\t\t\t\t");
                s.append(j.getCustomersServed());
                s.append("\t\t\t\t\t");
                s.append(String.format("%.2f", j.getSatisfactionRate()));
                s.append("\t\t\t ");
                s.append(j.getOverAllCost());
                s.append("\n");

                //switch statements decides which subcase we are in and adds it to the sample of each StatisticsUtils
                // array accordingly.
                switch (j.getCapacity()) {
                    //subcase when van capacity is 12
                    case 12:
                        CustServedStats[0].addValue(j.getCustomersServed());
                        SatisfactionRateStats[0].addValue(j.getSatisfactionRate());
                        CostStats[0].addValue(j.getOverAllCost());
                        break;
                    //subcase when van capacity is 18
                    case 18:
                        CustServedStats[1].addValue(j.getCustomersServed());
                        SatisfactionRateStats[1].addValue(j.getSatisfactionRate());
                        CostStats[1].addValue(j.getOverAllCost());
                        break;
                    //subcase when van capacity is 30
                    case 30:
                        CustServedStats[2].addValue(j.getCustomersServed());
                        SatisfactionRateStats[2].addValue(j.getSatisfactionRate());
                        CostStats[2].addValue(j.getOverAllCost());
                        break;
                }
            }
        }
        s.append(">-----------------------------------------------<\n");

        //build confidence intervals section.
        s.append("Confidence Intervals \n");
        //add headers to table
        s.append(" Van Capacity:\t\t\t12\t\t\t\t\t\t\t\t\t\t18\t\t\t\t\t\t\t\t\t\t30\n");
        s.append(" Customers Served:\t");

        //print out confidence interval for customers served for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < CustServedStats.length; i++) {
            s.append(String.format(sfinterval,
                                   CustServedStats[i].getMean(),
                                   getConfidenceInterval(CustServedStats[i]),
                                   CustServedStats[i].getMean(),
                                   getConfidenceInterval(CustServedStats[i])));
            s.append("\t");
        }

        s.append("\n Satisfaction Rate:\t");
        //print out confidence interval for Satisfaction Rate for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < SatisfactionRateStats.length; i++) {
            s.append(String.format(sfinterval,
                                   SatisfactionRateStats[i].getMean(),
                                   getConfidenceInterval(SatisfactionRateStats[i]),
                                   SatisfactionRateStats[i].getMean(),
                                   getConfidenceInterval(SatisfactionRateStats[i])));
            s.append("\t");
        }

        s.append("\n Cost:\t\t\t\t");
        //print out confidence interval for Cost for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < CostStats.length; i++) {
            s.append(String.format(sfinterval,
                                   CostStats[i].getMean(),
                                   getConfidenceInterval(CostStats[i]),
                                   CostStats[i].getMean(),
                                   getConfidenceInterval(CostStats[i])));
            s.append("\t");
        }
        s.append("\n >-----------------------------------------------<\n");
        //return print out of the table
        return s.toString();
    }

    /*
    Modified version of the above method that returns a comparative print out between two cases. They are given as
    parameters.
     */
    public String ShowDifferenceTable(List<List<Result>> difference1, List<List<Result>> difference2) {
        // array of size 3 to store the separate subcases when van capacity is equal to 12, 18, and 30.
        StatisticsUtils[] diffCustServedStats = new StatisticsUtils[3];
        StatisticsUtils[] diffSatisfactionRateStats = new StatisticsUtils[3];
        StatisticsUtils[] diffCostStats = new StatisticsUtils[3];
        //initialize the arrays
        for (int i = 0; i < diffCustServedStats.length; i++) {
            diffCustServedStats[i] = new StatisticsUtils();
            diffSatisfactionRateStats[i] = new StatisticsUtils();
            diffCostStats[i] = new StatisticsUtils();
        }
        //Initialize StringBuilder
        StringBuilder s = new StringBuilder();
        //Initialize counter to keep track of the current run.
        int runNumber = 1;
        //add header to table.
        s.append("Run Number \t Van Capacity \t Customers Served \t Customer Satisfaction \t Cost");
        s.append("\n");

        //Iterate through and calculate the difference then add to table, ensuring adequate spacing
        for (int i = 0; i < difference1.size(); i++) {
            runNumber = 1;
            for (int j = 0; j < difference1.get(i).size(); j++) {
                    //add data to table row by row
                    s.append(runNumber);
                    s.append("\t\t\t\t");
                    s.append(difference1.get(i).get(j).getCapacity());
                    s.append("\t\t\t\t\t");
                    s.append(difference1.get(i).get(j).getCustomersServed()
                                     - difference2.get(i).get(j).getCustomersServed());
                    s.append("\t\t\t\t\t");
                    s.append(String.format("%4.2f",
                                           difference1.get(i).get(j).getSatisfactionRate()
                                                   - difference2.get(i).get(j).getSatisfactionRate()));
                    s.append("\t\t\t ");
                    s.append(difference1.get(i).get(j).getOverAllCost()
                                     - difference2.get(i).get(j).getOverAllCost());
                    s.append("\n");
                    runNumber++;

                    //three different subcases. Data is added to sample depending if its the subcase with capacity of 12,
                    // 18, 30 for the vans.
                    if (difference1.get(i).get(j).getCapacity() == difference2.get(i).get(j).getCapacity() ) {
                        diffCustServedStats[i].addValue(difference1.get(i).get(j).getCustomersServed()
                                                                - difference2.get(i).get(j).getCustomersServed());
                        diffSatisfactionRateStats[i].addValue(difference1.get(i).get(j).getSatisfactionRate()
                                                                      - difference2.get(i).get(j).getSatisfactionRate());
                        diffCostStats[i].addValue(difference1.get(i).get(j).getOverAllCost()
                                                          - difference2.get(i).get(j).getOverAllCost());
                    }
            }
        }
        s.append(">-----------------------------------------------<\n");

        //build confidence intervals
        s.append("Confidence Intervals \n");
        s.append(" Van Capacity:\t\t\t12\t\t\t\t\t\t\t\t\t\t18\t\t\t\t\t\t\t\t\t\t30\n");
        s.append(" Customers Served:\t");

        //print out confidence interval for customers served for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < diffCustServedStats.length; i++) {
            //add row for all the three sub cases for the number of customers served.
            s.append(String.format(sfinterval,
                                   diffCustServedStats[i].getMean(),
                                   getConfidenceInterval(diffCustServedStats[i]),
                                   diffCustServedStats[i].getMean(),
                                   getConfidenceInterval(diffCustServedStats[i])));
            s.append("\t");
        }

        s.append("\n Satisfaction Rate:\t");
        //print out confidence interval for Satisfaction Rate for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < diffSatisfactionRateStats.length; i++) {
            //add row for all the three sub cases for Satisfaction rate.
            s.append(String.format(sfinterval,
                                   diffSatisfactionRateStats[i].getMean(),
                                   getConfidenceInterval(diffSatisfactionRateStats[i]),
                                   diffSatisfactionRateStats[i].getMean(),
                                   getConfidenceInterval(diffSatisfactionRateStats[i])));

            s.append("\t");
        }

        s.append("\n Cost:\t\t\t\t");
        //print out confidence interval for Satisfaction Rate for the subcases when van capacity is 12, 18, 30
        for (int i = 0; i < diffCostStats.length; i++) {
            //add row for all the three sub cases for overall cost.
            s.append(String.format(sfinterval,
                                   diffCostStats[i].getMean(),
                                   getConfidenceInterval(diffCostStats[i]),
                                   diffCostStats[i].getMean(),
                                   getConfidenceInterval(diffCostStats[i])));
            s.append("\t");
        }
        s.append("\n >-----------------------------------------------<\n");
        //return print out of table.
        return s.toString();
    }

    /*
    Helper method to compute zeta for confidence intervals.
     */
    private double getConfidenceInterval(StatisticsUtils stat) {
        double sd = stat.getStdDev();
        double rootn = Math.sqrt(NUMRUNS);
        double a = 1-(1-(confidence*0.01));
        double confidenceFactor = Probability.studentT(NUMRUNS - 1, a);
//t value for 95% confidence interval is ~1.96.
        return 1.96 * (sd / rootn);
    }
}


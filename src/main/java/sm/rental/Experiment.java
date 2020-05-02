// File: Experiment.java
// Description:
package sm.rental;

import sm.rental.model.*;
import cern.jet.random.engine.*;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

// Main Method: Experiments

public class Experiment {
    private static List<List<Result>> case1;
    private static List<List<Result>> case2;
    private static List<List<Result>> case3;
    private static List<List<Result>> improvedcase1;
    private static List<List<Result>> improvedcase2;
    private static List<List<Result>> improvedcase3;


    private static String RESULT_LIST_FORMAT =
            "Result Summary for capacity %d { Avg cost: %.2f, Avg Satisfaction: %.2f Number of Vans: %d, Number of Agents: %d }%n";
    private static int MAX_RENTAL_AGENTS = 99;
    private static int MAX_VANS = 99;

    public static void main(String[] args)
    {
        int NUMRUNS = 100;
        double confidence = 95;
        double startTime = 0.0, endTime = 270.0;


        // Lets get a set of uncorrelated seeds
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        ArrayList<Seeds> seeds = new ArrayList<>();
        IntStream.range(0, NUMRUNS)
                .forEach(i->seeds.add(new Seeds(rsg)));

        List<List<Result>> caseOneResultsBase = caseOne(NUMRUNS, startTime, endTime, seeds, false);

        /**
         * BASE EXPERIMENTS
         * SEARCHING FOR A 85% SATISFACTION RATE
         */
        case1 = caseOneResultsBase;

        List<List<Result>> caseTwoResultsBase = caseTwo(caseOneResultsBase, NUMRUNS, startTime,
                                                        endTime, seeds, false);

        case2 = caseTwoResultsBase;
        List<List<Result>> caseThreeResultsBase = caseTwoResultsBase.stream()
                .map(r -> caseThree(r, NUMRUNS, startTime, endTime, seeds, false))
                .collect(toList());

        case3 = caseThreeResultsBase;
        /*
        Save all three cases and build a DisplayResult object to work with.
         */
        List<List<Result>> caseOneResultsImproved = caseOne(NUMRUNS, startTime, endTime, seeds, true);

        /**
         * IMPROVED EXPERIMENTS
         * SEARCHING FOR A 90% SATISFACTION RATE
         */
        improvedcase1 = caseOneResultsImproved;

        List<List<Result>> caseTwoResultsImproved = caseTwo(caseOneResultsImproved, NUMRUNS, startTime,
                                                            endTime, seeds, true);

        improvedcase2 = caseTwoResultsImproved;
        List<List<Result>> caseThreeResultsImproved = caseTwoResultsImproved.stream()
                .map(r -> caseThree(r, NUMRUNS, startTime, endTime, seeds, true))
                .collect(toList());


        improvedcase3 = caseThreeResultsImproved;

        DisplayResult improvedCases = new DisplayResult(case1,
                                                        case2,
                                                        case3,
                                                        improvedcase1,
                                                        improvedcase2,
                                                        improvedcase3,
                                                        NUMRUNS,
                                                        confidence);
        System.out.println("Printing Base case 1\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getCase1()));
        System.out.println("Printing Base case 2\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getCase2()));
        System.out.println("Printing Base case 3\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getCase3()));
        System.out.println("Printing improved case 1\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getImprovedCase1()));
        System.out.println("Printing improved case 2\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getImprovedCase2()));
        System.out.println("Printing improved case 3\n");
        System.out.println(improvedCases.ShowTable(improvedCases.getImprovedCase3()));
        System.out.println("Printing Diff case 2 - case 3");
        System.out.println(improvedCases.ShowDifferenceTable(improvedCases.getCase2(),improvedCases.getCase3()));
        System.out.println("Printing Diff Improved case 2 - Improved case 3");
        System.out.println(improvedCases.ShowDifferenceTable(improvedCases.getImprovedCase2(),improvedCases.getImprovedCase3()));
        System.out.println("Printing Diff Improved case 3 - Improved case 3");
        System.out.println(improvedCases.ShowDifferenceTable(improvedCases.getCase3(),improvedCases.getImprovedCase3()));

    }

    /**
     * Searches for the minimum amount of vans to satisfy the customer satisfaction rate
     * Does so by Iterating on possible configurations between 1 and NUMRUNs for each capacity.
     *  It will run NUMRUN simulations with a different seed
     */

    public static List<List<Result>> caseOne(int NUMRUNS,
                                             double startTime,
                                             double endTime,
                                             ArrayList<Seeds> seeds,
                                             boolean improved){
        return IntStream.of(12, 18, 30)
                .mapToObj(capacity -> IntStream.range(1, MAX_VANS) // Stream that defines the number of vans
                        .sequential() // Ensures that the first result to meet base requirements will be the smallest van count
                        .mapToObj(vans -> IntStream.range(0, NUMRUNS)
                                .mapToObj(run -> RunSimulation(startTime, endTime, capacity,
                                                               vans, MAX_RENTAL_AGENTS, seeds.get(run)))
                                .collect(toList()))
                        .filter(resultList -> resultListMeetsRequirements(resultList, improved)) // Verify that each simulation met the requirements
                        .findFirst()) // Return the first configuration that satisfies this, since it is sequential, it will be the mininmum van configuration
                .map(Optional::get) //Removes the optional type encapsulation returned by findfirst
                .collect(toList());

    }


    /**
     * For each result, that represents a minimal van configuration
     * Test configurations between 0 - NUMRUNS of rental agents
     * Choose the configuration with least amount of rental agents
     */


    public static List<List<Result>> caseTwo(List<List<Result>> caseOneResults,
                                             int NUMRUNS,
                                             double startTime,
                                             double endTime,
                                             List<Seeds> seeds,
                                             boolean improved){
        return caseOneResults.stream()
                .map(minVanResult -> IntStream.range(1, MAX_RENTAL_AGENTS)
                        .sequential()
                        .mapToObj(rentalAgents -> IntStream.range(0, NUMRUNS)
                                .sequential()
                                .mapToObj(run -> RunSimulation(startTime, endTime, minVanResult.get(0).getCapacity(),
                                                               minVanResult.get(0).getNumVans(), rentalAgents,
                                                               seeds.get(run)))
                                .collect(toList()))
                        .filter(resultList -> resultListMeetsRequirements(resultList, improved)) // Verify that each simulation met the base requirements
                        .findFirst()) // Return the first configuration that satisfies this, since it is sequential, it will be the mininmum van configuration
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    /**
     * Attempts to find a better solution for each van configuration
     * Returns the best found. If the original is the best, it will be returned.
     */

    public static List<Result> caseThree(List<Result> rl,
                                         int NUMRUNS,
                                         double startTime,
                                         double endTime,
                                         ArrayList<Seeds> seeds,
                                         boolean improved){
        List<Result> best = rl;
        do {
            Optional<List<Result>> attempt = Stream.of(best)
                    .map(res -> IntStream.range(0, res.get(0).getNumRentalAgents())
                            .sequential()
                            .mapToObj(rentalAgents -> IntStream.range(0, NUMRUNS)
                                    .mapToObj(run -> RunSimulation(startTime, endTime, res.get(0).getCapacity(),
                                                                   res.get(0).getNumVans() + 1, rentalAgents,
                                                                   seeds.get(run)))
                                    .collect(toList()))
                            .filter(resultList -> resultListMeetsRequirements(resultList, improved))
                            .min(Experiment::resultListsCompareAvg))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
            if(!attempt.isPresent())
                return best;

            List<Result> challenger = attempt.get();
            if( 0>= resultListsCompareAvg(challenger, best)) {
                best = attempt.get();
            } else return best;
        }while(true);
    }

    public static int resultListsCompareAvg(List<Result> rl1, List<Result> rl2){
        return Double.compare(rl1.stream()
                                      .mapToDouble(Result::getOverAllCost)
                                      .average().orElse(0.0),
                              rl2.stream()
                                      .mapToDouble(Result::getOverAllCost)
                                      .average().orElse(0.0));
    }

    public static double resultListsAvgCost(List<Result> rl1){
        return rl1.stream()
                .mapToDouble(Result::getOverAllCost)
                .average().orElse(0.0);
    }
    public static double resultListsAvgSatisfaction(List<Result> rl1){
        return rl1.stream()
                .mapToDouble(Result::getSatisfactionRate)
                .average().orElse(0.0);
    }

    public static boolean resultListMeetsRequirements(List<Result> resultList, boolean improved){
        return resultList.stream()
                .mapToDouble(Result::getSatisfactionRate)
                .average().orElse(0.0) >= ((improved)?
                Constants.TARGET_SATISFACTION_RATE_IMPROVED :
                Constants.TARGET_SATISFACTION_RATE_BASE);
    }

    public static void printResultList(List<Result> lst){
        System.out.format(RESULT_LIST_FORMAT,
                          lst.get(0).getCapacity(),
                          resultListsAvgCost(lst),
                          resultListsAvgSatisfaction(lst),
                          lst.get(0).getNumVans(),
                          lst.get(0).getNumRentalAgents());
    }

    public static Result RunSimulation( double startTime,
                                        double endTime,
                                        int capacity,
                                        int numVans,
                                        int numRentalAgents,
                                        Seeds sds) {
        SMRental sm = new SMRental(startTime, endTime, capacity, numVans, numRentalAgents, sds);
        sm.runSimulation();
        return new Result(sm, sm.getSsovs(), sm.getDsovs());
    }
}

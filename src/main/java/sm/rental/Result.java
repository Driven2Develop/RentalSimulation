package sm.rental;

import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.outputs.DSOV;
import sm.rental.model.outputs.SSOV;

import java.util.Comparator;
import java.util.List;


@ToString
@Value
public class Result {
    @Getter private final Double satisfactionRate;
    @Getter private final Integer satisfiedCustomers;
    @Getter private final Integer customersServed;
    @Getter private final Double overAllCost;
    @Getter private final Double vanCost;
    @Getter private final Integer numVans;
    @Getter private final Integer numRentalAgents;
    @Getter private final Integer capacity;

    public Result(SMRental sm, SSOV ssov, DSOV dsov){
        this.satisfactionRate = ssov.getCustomerSatisfactionRate();
        this.satisfiedCustomers = ssov.getNumSatisfied();
        this.customersServed = ssov.getNumServed();
        this.overAllCost = dsov.overAllCost();
        this.vanCost = dsov.vanCost();
        this.numVans = sm.getNumVans();
        this.numRentalAgents = sm.getNumRentalAgents();
        this.capacity = sm.getNumSeats();
    }

    public boolean meetsBaseRequirements(){
        return satisfactionRate >= Constants.TARGET_SATISFACTION_RATE_BASE;
    }

    public boolean meetsImprovedRequirements(){
        return satisfactionRate >= Constants.TARGET_SATISFACTION_RATE_IMPROVED;
    }

    public static Comparator<Result> bySatisfaction = Comparator.comparingDouble(
            e -> e.satisfactionRate);
    public static Comparator<Result> byCost = Comparator.comparingDouble(e -> e.overAllCost);
}

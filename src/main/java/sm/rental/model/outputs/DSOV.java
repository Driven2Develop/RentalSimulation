package sm.rental.model.outputs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Van;

import java.util.Arrays;

import static sm.rental.model.Constants.DRIVER_COST;
import static sm.rental.model.Constants.RA_COST;

@RequiredArgsConstructor
public class DSOV {
    private static final double SHIFT = 4.5;
	@NonNull private final SMRental model;
	private final double perMileCost;

	public double vanCost(){
	    return (Arrays.asList(model.getRqVans()).stream()
                .mapToDouble(Van::getMileage)
                .sum() * perMileCost) +
                (model.getNumVans() * DRIVER_COST * SHIFT);
    }

	public double overAllCost(){
	    return vanCost() + (model.getNumRentalAgents() * RA_COST * SHIFT);
    }

    @Override
    public String toString() {
		return "DSOV[ overAllCost: "+overAllCost()+", vanCost: "+vanCost()+"]";
	}
}

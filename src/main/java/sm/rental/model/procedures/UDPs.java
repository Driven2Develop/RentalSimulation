package sm.rental.model.procedures;

import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.Customer.CustomerType;
import sm.rental.model.entities.Van;
import sm.rental.model.entities.Van.*;

import java.util.LinkedList;
import java.util.Optional;

import static sm.rental.model.Constants.ACCEPTABLE_N_TURNARROUNDT;
import static sm.rental.model.Constants.ACCEPTABLE_R_TURNARROUNDT;

public class UDPs {
	private static SMRental model;

	public static void ConfigureUDPs(SMRental smRental) {
	    model = smRental;
    }

    /**
     * Increments the numServed customers
     * If the customer is satisfied (Meets the corresponding turnaround time for the type of customer)
     * Updates the percent satisfied
     **/
    public static void HandleCustomerExit(Customer iCGCustomer) {

        double t = model.getClock();
	    if(iCGCustomer.getType() == CustomerType.NEW){
            if((t - iCGCustomer.getStartTime()) <= ACCEPTABLE_N_TURNARROUNDT) model.getSsovs().addSatisfiedCust();
            else model.getSsovs().addUnsatisfiedCust();
        } else {
            if((t - iCGCustomer.getStartTime()) <= ACCEPTABLE_R_TURNARROUNDT) model.getSsovs().addSatisfiedCust();
            else model.getSsovs().addUnsatisfiedCust();
        }
	}

    /**
     *  Determines if the specified van can unload at its location. A van can unload if the following conditions are met:
     *  If the van is at a location where it can drop off customers
     *  Van.Location=RENTAL_COUNTER or DROP_OFF
     *  The vans status is UNLOADING
     *  The van has customers
     **/
   /* public static boolean CanVanUnload(int vanid) {
        if(model.getRqVans()[vanid].getLocation() == Constants.DROP_OFF || model.getRqVans()[vanid].getLocation() == Constants.RENTAL_COUNTER)
            if(model.getRqVans()[vanid].getStatus() == VanStatus.UNLOADING)
                if(model.getRqVans()[vanid].getN()>0)
                    return true;
        return false;
    }*/

    /**
     *  Determines if the specified van can load a customer at its location. If the following conditions must be met:
     *  The vans status is LOADING
     *  UDP.GetCustomersAwaiting(Van.location) returns a Queue (awaitingQueue)
     *  UDP.GetCustomerForBoaring(awaitingQueue, Van.seatsAvailable) returns a Customer.
     **/
    public static boolean CanVanLoad(int vanid) {
        if(model.getRqVans()[vanid].getStatus() == VanStatus.LOADING) {
                return GetFirstAppropriateCustomer(vanid).isPresent();
        }
        return false;
    }

    /**
     *  Returns the first customer in the queue which can fit the specified amount of seats, otherwise returns false.
     *  A customer can fit if Customer.numPassengers <= seatsAvailable.
     **/
    public static Optional<Customer> GetFirstAppropriateCustomer(int vanid) {
        return model.getVanWaitLine()[model.getRqVans()[vanid].getLocation()].stream()
                .filter(c -> c.getNumPassengers() <= model.getRqVans()[vanid].getSeatsAvailable())
                .findFirst();
    }
}

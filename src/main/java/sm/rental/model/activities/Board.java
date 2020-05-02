package sm.rental.model.activities;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Van;
import sm.rental.model.entities.Van.VanStatus;
import sm.rental.model.entities.Customer;
import simulationModelling.ConditionalActivity;
import sm.rental.model.procedures.RVPs;
import sm.rental.model.procedures.UDPs;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

@RequiredArgsConstructor
public class Board extends ConditionalActivity {
    @NonNull private final SMRental model;
    private Customer icgcustomer = null;
    private Integer vanid = null;

    public static boolean precondition (SMRental model){
        return getVanForBoarding(model).isPresent();
    }

    @SneakyThrows
    public void startingEvent(){
        OptionalInt possibleVanID = getVanForBoarding(model);
        if(!possibleVanID.isPresent())
            throw new RuntimeException("Event Started but precondition must've been false: No vanid present");
        vanid = possibleVanID.getAsInt();
        Optional<Customer> possibleCustomer = getCustomerForBoarding(model, vanid);
        if(!possibleCustomer.isPresent())
            throw new RuntimeException("Event Started but precondition must've been false: No icgcustomer present");
        icgcustomer = possibleCustomer.get();
        model.getRqVans()[vanid].setStatus(VanStatus.BOARDING);
    }

    protected double duration(){
        return RVPs.uBoardingTime(icgcustomer.getNumPassengers());
    }


    public void terminatingEvent(){
        model.getRqVans()[vanid].addCustomer(icgcustomer);
        model.getRqVans()[vanid].setStatus(VanStatus.LOADING);
    }

    // Local User Defined procedures
    /**
     * Returns a vanid that can load a icgcustomer at its location.
     * Searches a vanid and returns the first vanid that UDP.CanLoadVan(Van) returns true for.
     * Otherwise returns false.
     **/
    private static OptionalInt getVanForBoarding(SMRental model) {
        return Arrays.stream(model.getRqVans())
                .mapToInt(Van::getVanId)
                .filter(UDPs::CanVanLoad)
                .findFirst();
    }

    /**
     * Finds the first appropriate icgcustomer at the vans location for boarding.
     * Otherwise returns false.
     * Uses UDP.GetCustomersAwaiting(Van.Location) to get the queue of customers (awaitingQueue)
     * as input to UDP.GetFirstAppropriateCustomer(awaitingQueue, Van.seatsAvailable)
     **/
    private static Optional<Customer> getCustomerForBoarding(SMRental model, int vanid) {
        Optional<Customer> possibleCustomer = UDPs.GetFirstAppropriateCustomer(vanid);
        possibleCustomer.ifPresent(model.getVanWaitLine()[model.getRqVans()[vanid].getLocation()]::remove);
        return possibleCustomer;
    }

    // Predicate
    public static final Function<SMRental,Optional<ConditionalActivity>> function = (SMRental model) -> {
        if(Board.precondition(model))
            return Optional.of(new Board(model));
        else return Optional.empty();
    };
}

package sm.rental.model.activities;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import simulationModelling.ConditionalActivity;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.Customer.CustomerType;
import sm.rental.model.entities.Van;
import sm.rental.model.entities.Van.VanStatus;
import sm.rental.model.procedures.RVPs;
import sm.rental.model.procedures.UDPs;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

@RequiredArgsConstructor
public class ExitVan extends ConditionalActivity {
    @NonNull
    private final SMRental model;
    private Integer vanid = null;
    private Customer icgcustomer = null;

    public static boolean precondition(SMRental model) {
        return getVanForUnloading(model).isPresent();
    }

    public void startingEvent() {
        OptionalInt possibleVanid = getVanForUnloading(model);
        if (! possibleVanid.isPresent())
            throw new RuntimeException("Event Started but precondition must've been false: No vanid present");
        vanid = possibleVanid.getAsInt();
        Optional<Customer> possibleCustomer = model.getRqVans()[vanid].removeNextCustomer();
        if (! possibleCustomer.isPresent())
            throw new RuntimeException("Event Started but precondition must've been false: No icgcustomer present in vanid");
        icgcustomer = possibleCustomer.get();
        model.getRqVans()[vanid].setStatus(VanStatus.EXITING);
    }

    public double duration() {
        return RVPs.uExitingTime(icgcustomer.getNumPassengers());
    }

    public void terminatingEvent() {
        if (model.getRqVans()[vanid].getSeatsAvailable() == model.getNumSeats())
            model.getRqVans()[vanid].setStatus(VanStatus.LOADING);
        else
            model.getRqVans()[vanid].setStatus(VanStatus.UNLOADING);
        if (icgcustomer.getType() == CustomerType.NEW)
            model.getRentalLine().offerLast(icgcustomer);
        else
            UDPs.HandleCustomerExit(icgcustomer);
    }

    // Local User Defined Procedures

    /**
     * The vanid is located at rental counter or drop off point (Van.Location=RENTAL_COUNTER or DROP_OFF)
     * The vans status is unloading
     * The vanid is not empty
     **/
    private static OptionalInt getVanForUnloading(SMRental model) {
        return Arrays.stream(model.getRqVans())
                .filter(van -> van.getStatus() == VanStatus.UNLOADING && van.getN() > 0)
                .mapToInt(Van::getVanId)
                .findFirst();
    }

    // Predicate
    public static final Function<SMRental, Optional<ConditionalActivity>> function = (SMRental model) -> {
        if (ExitVan.precondition(model)) {
            return Optional.of(new ExitVan(model));
        } else
            return Optional.empty();
    };
}

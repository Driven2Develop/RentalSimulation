package sm.rental.model.activities;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import simulationModelling.ConditionalActivity;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.Customer.CustomerType;
import sm.rental.model.entities.RentalCounter;
import sm.rental.model.procedures.RVPs;
import sm.rental.model.procedures.UDPs;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class Service extends ConditionalActivity {
    @NonNull private final SMRental model;

    private Customer icgcustomer;

    public static boolean precondition(SMRental model) {
        return (model.getRentalLine().size() > 0)
                && isAgentAvailable(model.getRentalCounter());
    }
    public void startingEvent() {
        occupyAgent();
        icgcustomer = model.getRentalLine().pop(); // Remove icgcustomer
        if(icgcustomer == null)
            throw new RuntimeException("Couldn't service");
    }

    public double duration() {
        return RVPs.uServiceTime(icgcustomer.getType());
    }

    public void terminatingEvent() {
        freeAgent();
        if(icgcustomer.getType() == CustomerType.NEW)
            UDPs.HandleCustomerExit(icgcustomer);
        else
            model.getVanWaitLine()[Constants.RENTAL_COUNTER].offerLast(icgcustomer);
    }

    //Local User Defined Procedures
    private static boolean isAgentAvailable(RentalCounter rc){
        return rc.getUNumAgents() > 0;
    }

    private void freeAgent(){
        model.getRentalCounter().addAgent();
    }

    private void occupyAgent(){
        model.getRentalCounter().removeAgent();
    }

    // Predicate

    public static final Function<SMRental, Optional<ConditionalActivity>> function = (SMRental model) -> {
        if(Service.precondition(model))
            return Optional.of(new Service(model));
        else return Optional.empty();
    };
}

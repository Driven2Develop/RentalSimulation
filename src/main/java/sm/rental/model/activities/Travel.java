package sm.rental.model.activities;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import simulationModelling.ConditionalActivity;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;

import sm.rental.model.entities.Van;
import sm.rental.model.entities.Van.*;
import sm.rental.model.procedures.DVPs;
import sm.rental.model.procedures.UDPs;

import javax.jws.Oneway;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

@RequiredArgsConstructor
public class Travel extends ConditionalActivity {
    @NonNull private final SMRental model;
    private Integer vanid = null;
    private int nextDestination = -1;

    private static final double DISTANCE_T1_T2 = 0.3;
    private static final double DISTANCE_T2_RC = 2.0;
    private static final double DISTANCE_RC_T1 = 1.5;
    private static final double DISTANCE_RC_DP = 1.7;
    private static final double DISTANCE_DP_T1 = 0.5;

    public static boolean precondition(SMRental model){
        return getVanForTravel(model).isPresent();
    }

    public void startingEvent() {
        OptionalInt possibleVanID = getVanForTravel(model);
        if(!possibleVanID.isPresent())
            throw new RuntimeException("Event Started but precondition must've been false: No vanid present");
        vanid = possibleVanID.getAsInt();
        nextDestination = getNextDestinationForVan(model, vanid);
        model.getRqVans()[vanid].setStatus(VanStatus.TRAVELLING);
    }

    public double duration(){
        return DVPs.travelTime(getDistanceTravelled( model.getRqVans()[vanid].getLocation(), nextDestination));
    }

    public void terminatingEvent(){
        model.getRqVans()[vanid].addMileage(getDistanceTravelled(model.getRqVans()[vanid].getLocation(),
                                                                 nextDestination));
        model.getRqVans()[vanid].setLocation(nextDestination);
        if(( nextDestination == Constants.RENTAL_COUNTER || nextDestination == Constants.DROP_OFF )
                && model.getRqVans()[vanid].getN() != 0)
            model.getRqVans()[vanid].setStatus(VanStatus.UNLOADING);
        else model.getRqVans()[vanid].setStatus(VanStatus.LOADING);
    }

    //Local User Defined Procedures
    /**
     * A vanid cannot load anymore customers at its location and
     * the vanid cannot unload anymore customers at its location
     **/
    private static OptionalInt getVanForTravel(SMRental model){
        return Arrays.stream(model.getRqVans())
                .filter(van -> van.getStatus() == VanStatus.LOADING && (!UDPs.CanVanLoad(van.getVanId())))
                .mapToInt(Van::getVanId)
                .findFirst();
    }

    /**
     * If the vanid is at Terminal 1, then it will go to Terminal 2
     * If the vanid is at Terminal 2, then it will go to the Rental Counter
     * If the vanid is located at rental counter, then if it has 0 customer, then it will go to terminal 1. Otherwise it will go to the drop off point.
     * If the vanid is at the drop off point, then it will go to terminal 1
     * Otherwise Throws error
     **/
    private static int getNextDestinationForVan(SMRental model, int vanid){
        switch (model.getRqVans()[vanid].getLocation()){
            case Constants.TERMINAL1:
                 return Constants.TERMINAL2;
            case Constants.TERMINAL2:
                 return Constants.RENTAL_COUNTER;
            case Constants.RENTAL_COUNTER:
                if(model.getRqVans()[vanid].getN() > 0)
                     return Constants.DROP_OFF;
                else
                     return Constants.TERMINAL1;
            case Constants.DROP_OFF:
                 return Constants.TERMINAL1;
            default:
                throw new IllegalStateException("Van Location doesn't exist");
        }
    }

    public static double getDistanceTravelled(int from, int to){
        switch (to){
            case Constants.TERMINAL1:
                if(from == Constants.RENTAL_COUNTER) return DISTANCE_RC_T1;
                else return DISTANCE_DP_T1;
            case Constants.TERMINAL2:
                return DISTANCE_T1_T2;
            case Constants.RENTAL_COUNTER:
                return DISTANCE_T2_RC;
            case Constants.DROP_OFF:
                return DISTANCE_RC_DP;
        }
        throw new IllegalStateException("Destination doesn't exist");
    }

    // Predicate
    public static final Function<SMRental, Optional<ConditionalActivity>> function = (SMRental model) -> {
        if(Travel.precondition(model))
            return Optional.of(new Travel(model));
        else return Optional.empty();
    };
}

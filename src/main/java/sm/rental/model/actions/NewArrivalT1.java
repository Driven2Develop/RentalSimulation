package sm.rental.model.actions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import simulationModelling.ScheduledAction;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.Customer.CustomerType;
import sm.rental.model.procedures.RVPs;

@RequiredArgsConstructor
public class NewArrivalT1 extends ScheduledAction {
    @NonNull private final SMRental model;

    public double timeSequence(){
        return RVPs.DuNCustomerT1();
    }

    public void actionEvent(){
        Customer customer = new Customer(model.getClock(), CustomerType.NEW, RVPs.uNumPassengers() + 1);
        model.getVanWaitLine()[Constants.TERMINAL1].add(customer);
    }
}
package sm.rental.model.actions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import simulationModelling.ScheduledAction;
import sm.rental.model.Constants;
import sm.rental.model.SMRental;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.Van;

import java.util.ArrayList;
import java.util.LinkedList;

@RequiredArgsConstructor
public class Initialise extends ScheduledAction {
	@NonNull private final SMRental model;
	private double [] ts = { 0.0, -1.0 }; // -1.0 ends scheduling
	private int tsix = 0;  // set index to first entry.

	public double timeSequence() {
		return ts[tsix++];  // only invoked at t=0
	}

	public void actionEvent() {
        Van[] vans = model.getRqVans();
        int numVans = model.getNumVans();
        int vanCapacity = model.getNumSeats();
        for (int i = 0; i < numVans; i++)
            vans[i] = new Van(vanCapacity, i);
        LinkedList[] lines = model.getVanWaitLine();
        for(int i =0; i < lines.length; i++)
            lines[i] = new LinkedList<Customer>();
    }
}

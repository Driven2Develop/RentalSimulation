package sm.rental.model;

import java.util.*;
import java.util.function.Function;

import lombok.Getter;
import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;
import simulationModelling.ConditionalActivity;
import sm.rental.model.actions.NewArrivalT1;
import sm.rental.model.actions.NewArrivalT2;
import sm.rental.model.actions.ReturningArrival;
import sm.rental.model.activities.Board;
import sm.rental.model.activities.ExitVan;
import sm.rental.model.activities.Service;
import sm.rental.model.activities.Travel;
import sm.rental.model.entities.Customer;
import sm.rental.model.entities.RentalCounter;
import sm.rental.model.entities.Van;
import sm.rental.model.outputs.DSOV;
import sm.rental.model.outputs.SSOV;
import sm.rental.model.procedures.RVPs;
import sm.rental.model.actions.Initialise;
import sm.rental.model.procedures.UDPs;

import static java.util.stream.Collectors.toList;

//
// The Simulation model Class
public class SMRental extends AOSimulationModel
{
	// Constants available from Constants class
	/* Parameter */
	@Getter private int numSeats;
	@Getter private int numVans;
	@Getter private int numRentalAgents;

	/*-------------Entity Data Structures-------------------*/
	/* Group and Queue entities */
	// Define the reference variables to the various 
	// entities with scope Set and Unary
	@Getter private Van [] rqVans;
	@Getter private RentalCounter rentalCounter;

	@Getter private LinkedList<Customer>[] vanWaitLine;

    @Getter private LinkedList<Customer> rentalLine;

	// Objects can be created here or in the Initialise Action
    private List<Function<SMRental,Optional<ConditionalActivity>>> preconditions;
    // SSOV object
	@Getter private SSOV ssovs;
    // DSOV object
    @Getter private DSOV dsovs;

    //End time
    @Getter private double endTime;

    // Constructor
	public SMRental(double t0time, double tftime, int numSeats, int numVans, int numRentalAgents, Seeds sd) {
        // Setup procedures
        UDPs.ConfigureUDPs(this);
        RVPs.ConfigureRVPs(this, sd);

        // Configure Van cost
        double perMileCost;
        if(numSeats == 12) {
            perMileCost = Constants.VAN12_COST;
        } else if(numSeats == 18) {
            perMileCost = Constants.VAN18_COST;
        } else {
            perMileCost = Constants.VAN30_COST;
        }

        // Setup outputs
        ssovs = new SSOV(this);
        dsovs = new DSOV(this, perMileCost);

        // Initialise parameters here
		this.numSeats = numSeats;
		this.numVans = numVans;
		this.numRentalAgents = numRentalAgents;

		// Create Structural Entities Corresponding to Resources

        vanWaitLine = new LinkedList [4]; // because good practices went out the window awhile ago
        rentalLine = new LinkedList<Customer>();
        rentalCounter = new RentalCounter(numRentalAgents);
        rqVans = new Van[numVans];

        // Setup preconditions
        preconditions = new LinkedList<>();
        preconditions.add(Service.function);
        preconditions.add(Travel.function);
        preconditions.add(Board.function);
        preconditions.add(ExitVan.function);

        // Initialise the simulation model
		initAOSimulModel(t0time,tftime);
		endTime = tftime;

        // Schedule the first arrivals and employee scheduling
		Initialise init = new Initialise(this);
		scheduleAction(init);  // Should always be first one scheduled.
		// Schedule other scheduled actions and acitvities here
        NewArrivalT1 arrt1 = new NewArrivalT1(this);
        scheduleAction(arrt1);
        NewArrivalT2 arrt2 = new NewArrivalT2(this);
        scheduleAction(arrt2);
        ReturningArrival returningArrival = new ReturningArrival(this);
        scheduleAction(returningArrival);
        printDebug();

	}

	/************  Implementation of Data Modules***********/	
	/*
	 * Testing preconditions
	 */

    @Override
    public void testPreconditions(Behaviour behObj) {
        reschedule(behObj);
        while(scanPreconditions());
    }

    // Single scan of all preconditions
    // Returns true if at least one precondition was true.
    private boolean scanPreconditions() {
        List<ConditionalActivity> possibleActivities = preconditions.stream()
                .map(e -> e.apply(this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
        if(possibleActivities.isEmpty()) return false;
        possibleActivities.forEach(act -> {
            act.startingEvent();
            scheduleActivity(act);
        });
        return true;
    }

    public void eventOccured() {
        if(logFlag) printDebug();
    }

    // for Debugging
    boolean logFlag = true;
    private void printDebug() {
        if (logFlag)
            return;
        // Debugging
        System.out.println(">-----------------------------------------------<");

        System.out.println("Clock:" + getClock() + "Q.VanWaitLineTerminal1.n:" + vanWaitLine[0].size()+
                                   " Q.Terminal2.n:" + vanWaitLine[1].size() +" Q.RentalLine.n: "+ rentalLine.size()+
                "Q.ReturnLine.n:" + vanWaitLine[2].size()  +"RG.Vans:" );

        StringBuilder vanstringsBuilder = new StringBuilder("[");
        Arrays.asList(rqVans).forEach(v -> vanstringsBuilder.append(v.toString()).append("\n"));
        String vanstrings = vanstringsBuilder.append("]").toString();
        System.out.println(vanstrings);
        System.out.println(ssovs.toString() +"\n" +dsovs.toString());
        //showSBL();
        System.out.println(">-----------------------------------------------<");
    }
}

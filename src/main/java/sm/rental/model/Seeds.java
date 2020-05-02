package sm.rental.model;

import cern.jet.random.engine.RandomSeedGenerator;
import lombok.Getter;

public class Seeds 
{
	@Getter private int newCustomerSeedT1;   // New Customers at terminal 1 seed
	@Getter private int newCustomerSeedT2;   // New Customers at terminal 2 seed
	@Getter private int returningCustomerSeed;   // Returning Customer Arrival seed

	@Getter private int additionalPassengerSeed;   // additional passenger seed
	@Getter private int boardingTimeSeed;   // boarding time seed
	@Getter private int exitTimeSeed;	  // exiting time seed

	@Getter private int newCustomerServiceTimeSeed;   // new customer seed
	@Getter private int returningCustomerServiceTimeSeed;   // returning customer seed

	public Seeds(RandomSeedGenerator rsg) {
		newCustomerSeedT1 = rsg.nextSeed();
		newCustomerSeedT2 = rsg.nextSeed();
		returningCustomerSeed = rsg.nextSeed();
		additionalPassengerSeed = rsg.nextSeed();
		boardingTimeSeed = rsg.nextSeed();
		exitTimeSeed = rsg.nextSeed();
		newCustomerServiceTimeSeed = rsg.nextSeed();
		returningCustomerServiceTimeSeed = rsg.nextSeed();
	}
}

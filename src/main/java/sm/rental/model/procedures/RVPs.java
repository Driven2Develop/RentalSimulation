package sm.rental.model.procedures;

import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.Uniform;
import sm.rental.model.entities.Customer.CustomerType;
import sm.rental.model.SMRental;
import sm.rental.model.Seeds;

public class RVPs
{
	private static SMRental model; // for accessing the clock

    // Mean arrival times
    private static final double [] T1Means = {
            15, 7.5 , 2   , 4   , 3.33, 4.29, 4.62, 6   , 15  , 15, 15, 4.29, 3.75, 4, 8.57, 20, 15, 30 };
    private static final double [] T2Means = {
            20, 10  , 6.67, 4   , 3.53, 3.16, 4.29, 10  , 20  , 15, 2.86, 4.29, 3.16, 5, 12, 30, 20, 20 };
    private static final double [] RCMeans = {
            5 , 6.67, 3.33, 2.14, 2.61, 2.86, 3.75, 5.45, 3.53, 15, 1.67, 2.5, 1.875, 3.75, 4.62, 4.62, 12, 15 };

    // Percentage of additional passengers with customers
    private static final double Additional3 = 0.05;
    private static final double Additional2 = 0.2;
    private static final double Additional1 = 0.4;

    // Check in and check out, min and max times in minutes
    private static final double COMax = 5.1;
    private static final double COMin = 1.6;
    private static final double CIMax = 4.8;
    private static final double CIMin = 1;

    // Average exiting and boarding time
    private static final double avgBoardingTime = 0.2;
    private static final double avgExitTime = 0.1;

    private static Exponential NCustomerT1;
	private static Exponential NCustomerT2;
	private static Exponential RCustomer;
	private static Uniform RCServiceTime;
	private static Uniform NCServiceTime;
	private static MersenneTwister AdditionalPassengers;
	private static Exponential BoardingTime;
	private static Exponential ExitingTime;

	// Constructor
	public static void ConfigureRVPs(SMRental smRental, Seeds sd) {
		model = smRental;

		NCustomerT1 = new Exponential(1.0/T1Means[0], new MersenneTwister(sd.getNewCustomerSeedT1()));

		NCustomerT2 = new Exponential(1.0/T2Means[0], new MersenneTwister(sd.getNewCustomerSeedT2()));

		RCustomer = new Exponential(1.0/RCMeans[0], new MersenneTwister(sd.getReturningCustomerSeed()));

		RCServiceTime = new Uniform(CIMax, CIMin, sd.getReturningCustomerServiceTimeSeed());
		NCServiceTime = new Uniform(COMax, COMin, sd.getNewCustomerServiceTimeSeed());

		AdditionalPassengers = new MersenneTwister(sd.getAdditionalPassengerSeed());

		BoardingTime = new Exponential(1.0/ avgBoardingTime, new MersenneTwister(sd.getBoardingTimeSeed()));
		ExitingTime = new Exponential(1.0/ avgExitTime, new MersenneTwister(sd.getExitTimeSeed()));
	}

	public static double DuNCustomerT1(){
        double nextCustomer;
        double t = model.getClock();
        double mean = getMean(T1Means, t);
        nextCustomer = t + NCustomerT1.nextDouble(1.0/mean);
        if(nextCustomer > model.getEndTime())
            nextCustomer = -1.0;  // Ends time sequence
        return nextCustomer;
	}

	public static double DuNCustomerT2(){
        double nextCustomer;
        double t = model.getClock();
        double mean;
        mean = getMean(T1Means, t);
        nextCustomer = t+ NCustomerT2.nextDouble(1.0/mean);
        if(nextCustomer > model.getEndTime())
            nextCustomer = -1.0;  // Ends time sequence
        return nextCustomer;
	}

	public static double DuRCustomer(){
		double nextCustomer;
        double t = model.getClock();
        double mean;
		mean = getMean(RCMeans, t);
		nextCustomer = t+ RCustomer.nextDouble(1.0/mean);
        if(nextCustomer > model.getEndTime())
            nextCustomer = -1.0;  // Ends time sequence
		return nextCustomer;
	}

	//Check in time is associated with returning customers
	//Check out time is associated with new customers
	public static double uServiceTime(CustomerType uType){
		if(uType == CustomerType.RETURNING){
			return RCServiceTime.nextDouble();
		}
        return NCServiceTime.nextDouble();
	}

	public static int uNumPassengers(){
		double rand = AdditionalPassengers.nextDouble();
		if(rand < Additional3){
			return 3;
		} else if(rand < Additional2){
			return 2;
		} else if(rand < Additional1){
			return 1;
		} else {
			return 0;
		}
	}

	public static double uBoardingTime(int numPassengers){
		double boardingTime = 0;
		for(int i = 0; i < numPassengers; i++)
			boardingTime += BoardingTime.nextDouble();
		return boardingTime/60.0;
	}

	public static double uExitingTime(int numPassengers){
		double exitTime = 0;
		for(int i = 0; i < numPassengers; i++)
			exitTime += ExitingTime.nextDouble();
		return exitTime/60.0;
	}

	private static double getMean(double [] means, double t) {
		return means[(int)(t / 15)];
	}
}

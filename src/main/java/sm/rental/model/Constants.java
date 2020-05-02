package sm.rental.model;

public class Constants
{

    public static final int TERMINAL1 = 0;
    public static final int TERMINAL2 = 1;
    public static final int RENTAL_COUNTER = 2;
    public static final int DROP_OFF = 3;

    public static final int VAN_SPEED = 20; //The speed that a van transports in the system.

    public static final double BOARDING_TIME = 0.2; //the average time that a customer get on the van
    public static final double EXTiING_TINE = 0.1; //the average time that a customer get off the van

    public static final double DRIVER_COST = 12.5; //per hour salary of a driver
    public static final double RA_COST = 11.5; //per hour salary of a rental agent

    public static final double VAN12_COST = 0.48; //$0.48 per mile
    public static final double VAN18_COST = 0.73; //$0.73 per mile
    public static final double VAN30_COST = 0.92; //$0.92 per mile

    public static final int ACCEPTABLE_N_TURNARROUNDT = 20; //The acceptable turnaround time for new customers
    public static final int ACCEPTABLE_R_TURNARROUNDT = 18; //The acceptable turnaround time for returning customers

    public static final Double TARGET_SATISFACTION_RATE_BASE = 0.85;
    public static final Double TARGET_SATISFACTION_RATE_IMPROVED = 0.93;
}

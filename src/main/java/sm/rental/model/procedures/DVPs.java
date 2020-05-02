package sm.rental.model.procedures;


import static sm.rental.model.Constants.VAN_SPEED;

public class DVPs {

    private static final double SIXTY_MINUTES = 60.0;

    public static double travelTime(double distance){
        return distanceToTime(distance);
    }

    private static double distanceToTime(double distance){
        return distance/VAN_SPEED*SIXTY_MINUTES;
    }
}

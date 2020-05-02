package sm.rental.model.entities;

import lombok.ToString;
import lombok.Value;

@ToString
@Value
public class Customer {
    public enum CustomerType {
        NEW,
        RETURNING
    }

    private double startTime; //The time at which a customer enters the system.
    private CustomerType type;
    private int numPassengers; //The number of passengers accompanying the customer plus the customer themselves.
}

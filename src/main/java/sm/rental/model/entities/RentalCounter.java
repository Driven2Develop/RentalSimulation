package  sm.rental.model.entities;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@ToString
@RequiredArgsConstructor
public class RentalCounter {

    @NonNull @Getter private Integer uNumAgents; //The number of available agents.

    public void addAgent(){
        uNumAgents++;
    }
    public void removeAgent() throws RuntimeException {
        if(uNumAgents<=0) throw new RuntimeException("Agents can't be less than zero");
        uNumAgents--;
    }
}
package model.flights;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
public class RequestFlight {
    private Date departureDate;
    private Date arrivalDate;
    private String departureCity;
    private String arrivalCity;

}

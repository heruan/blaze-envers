package to.lova.blaze.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.blazebit.persistence.CTE;

@CTE
@Entity
public class TimestampCTE {

    @Id
    private Long t;

    public Instant getInstant() {
        return Instant.ofEpochMilli(this.t);
    }

}

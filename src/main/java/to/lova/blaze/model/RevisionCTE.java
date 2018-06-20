package to.lova.blaze.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.blazebit.persistence.CTE;

@CTE
@Entity
public class RevisionCTE {

    @Id
    Long timestamp;

    Long revision;

}

package to.lova.blaze.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("values(1)")
// FIXME Delete when https://github.com/Blazebit/blaze-persistence/issues/574
public class Dual {

    @Id
    Long id;

}

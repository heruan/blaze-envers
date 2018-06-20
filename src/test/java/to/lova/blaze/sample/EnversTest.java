package to.lova.blaze.sample;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
import javax.persistence.metamodel.EntityType;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.junit.Before;
import org.junit.Test;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinOnBuilder;
import com.blazebit.persistence.JoinType;

import to.lova.blaze.model.Cat;
import to.lova.blaze.model.Dual;
import to.lova.blaze.model.RevisionCTE;
import to.lova.blaze.model.TimestampCTE;

public class EnversTest {

    private EntityManager em;

    private CriteriaBuilderFactory cbf;

    @Before
    public void createCriteriaBuilderFactory() {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("default");
        this.em = emf.createEntityManager();
        this.cbf = Criteria.getDefault().createCriteriaBuilderFactory(emf);
    }

    @Test
    public void selectFromEnversEntityType() {
        EntityType<?> entityType = this.getAuditedEntityType(Cat.class);

        this.cbf.create(this.em, String.class).from(entityType, "aud")
                .select("aud.name").getResultList();
    }

    @Test
    public void filterFromEnversEntityType() {
        EntityType<?> entityType = this.getAuditedEntityType(Cat.class);
        this.cbf.create(this.em, String.class).from(entityType, "aud")
                .select("aud.name").where("aud.name_MOD").eq(true)
                .getResultList();
    }

    @Test
    public void joinOnEnversEntityType() {
        Instant now = Instant.now();
        long f = now.minus(30, ChronoUnit.DAYS).toEpochMilli();
        long t = now.toEpochMilli();
        long s = ChronoUnit.DAYS.getDuration().toMillis();
        EntityType<?> auditedEntityType = this.getAuditedEntityType(Cat.class);
        CriteriaBuilder<Tuple> timestamps = this.cbf
                .create(this.em, Tuple.class).withRecursive(TimestampCTE.class)
                .from(Dual.class).bind("t")
                .select("FUNCTION('CAST_BIGINTEGER', " + f + ")").unionAll()
                .from(TimestampCTE.class, "cte").bind("t")
                .select("cte.t + :inc").setParameter("inc", s).where("cte.t")
                .lt(t).end();
        CriteriaBuilder<Tuple> revisions = timestamps.with(RevisionCTE.class)
                .from(TimestampCTE.class, "timestamps")
                .joinOn(DefaultRevisionEntity.class, "r", JoinType.LEFT)
                .on("r.timestamp").leExpression("timestamps.t").end()
                .bind("timestamp").select("timestamps.t").bind("revision")
                .select("max(r.id)").groupBy("timestamps.t")
                .orderByAsc("timestamps.t").end();
        JoinOnBuilder<CriteriaBuilder<Tuple>> joinOnBuilder = revisions
                .from(RevisionCTE.class, "r").select("r.timestamp")
                .select("count(r.revision)")
                .joinOn(auditedEntityType, "aud", JoinType.LEFT)
                .on("aud.originalId.REV.id").leExpression("r.revision")
                .on("aud.REVTYPE").notEq(RevisionType.DEL).onOr()
                .on("aud.REVEND").gtExpression("r.revision").on("aud.REVEND")
                .isNull().endOr();
        joinOnBuilder.end().groupBy("r.timestamp").orderByAsc("r.timestamp")
                .getResultList().stream().collect(Collectors.toMap(tuple -> {
                    long timestamp = tuple.get(0, Long.class);
                    return Instant.ofEpochMilli(timestamp);
                }, tuple -> tuple.get(1, Long.class), (t1, t2) -> t1,
                        TreeMap::new));
    }

    private EntityType<?> getAuditedEntityType(Class<?> entityClass) {
        SessionFactoryImplementor session = this.em
                .unwrap(SessionImplementor.class).getSessionFactory();
        EnversService envers = session.getServiceRegistry()
                .getService(EnversService.class);

        MetamodelImplementor metamodel = session.getMetamodel();

        String entityName = metamodel.entityPersister(Cat.class)
                .getEntityName();
        String auditEntityName = envers.getAuditEntitiesConfiguration()
                .getAuditEntityName(entityName);

        return metamodel.entity(auditEntityName);
    }

}

package to.lova.blaze.sample;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.junit.Test;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;

import to.lova.blaze.model.Cat;

public class EnversTest {

    @Test
    public void selectFromEnversEntityType() {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        SessionFactoryImplementor session = em.unwrap(SessionImplementor.class)
                .getSessionFactory();
        EnversService envers = session.getServiceRegistry()
                .getService(EnversService.class);

        MetamodelImplementor metamodel = session.getMetamodel();

        String entityName = metamodel.entityPersister(Cat.class)
                .getEntityName();
        String auditEntityName = envers.getAuditEntitiesConfiguration()
                .getAuditEntityName(entityName);

        EntityType<Object> entityType = metamodel.entity(auditEntityName);

        CriteriaBuilderFactory cbf = Criteria.getDefault()
                .createCriteriaBuilderFactory(emf);
        cbf.create(em, String.class).from(entityType, "aud").select("aud.name")
                .getResultList();

    }

}

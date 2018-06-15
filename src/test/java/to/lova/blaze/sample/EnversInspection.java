/*-
 * Copyright 2017-2018 Axians SAIV S.p.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
-*/
package to.lova.blaze.sample;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.junit.Test;

import to.lova.blaze.model.Cat;

public class EnversInspection {

    private EntityManagerFactory emf;

    @Test
    public void inspectEnversMetamodel() {
        this.emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = this.emf.createEntityManager();
        SessionFactoryImplementor session = em.unwrap(SessionImplementor.class)
                .getSessionFactory();
        EnversService envers = session.getServiceRegistry()
                .getService(EnversService.class);

        MetamodelImplementor metamodel = session.getMetamodel();

        String entityName = metamodel.entityPersister(Cat.class)
                .getEntityName();
        String auditEntityName = envers.getAuditEntitiesConfiguration()
                .getAuditEntityName(entityName);

        EntityPersister persister = metamodel.entityPersister(auditEntityName);
        OuterJoinLoadable ojl = (OuterJoinLoadable) persister;

        String tableName = ojl.getTableName();

        System.out.println("Table: " + tableName);

        EntityType<Object> entityType = metamodel.entity(auditEntityName);

        for (Attribute<? super Object, ?> attribute : entityType
                .getAttributes()) {
            String attributeName = attribute.getName();
            System.out.println("Attribute: " + attributeName);
            String columns = Stream
                    .of(ojl.getPropertyColumnNames(attributeName))
                    .collect(Collectors.joining(","));
            System.out.println("Columns: " + columns);
        }

    }

}

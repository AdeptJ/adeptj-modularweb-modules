/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.commons.jdbc.DataSourceService;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.ConstructorCriteria;
import com.adeptj.modules.data.jpa.CrudDTO;
import com.adeptj.modules.data.jpa.DeleteCriteria;
import com.adeptj.modules.data.jpa.JpaCallback;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.JpaUtil;
import com.adeptj.modules.data.jpa.PersistenceException;
import com.adeptj.modules.data.jpa.QueryType;
import com.adeptj.modules.data.jpa.ReadCriteria;
import com.adeptj.modules.data.jpa.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.TupleQueryCriteria;
import com.adeptj.modules.data.jpa.UpdateCriteria;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import static com.adeptj.modules.data.jpa.JpaConstants.JPA_FACTORY_PID;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Implementation of {@link JpaRepository} based on EclipseLink JPA Reference Implementation
 * <p>
 * This will be registered with the OSGi service registry whenever there is a new EntityManagerFactory configuration
 * created from OSGi console.
 * <p>
 * Therefore there will be a separate service for each PersistenceUnit.
 * <p>
 * Callers will have to provide an OSGi filter while injecting a reference of {@link JpaRepository}
 *
 * <code>
 * &#064;Reference(target="(osgi.unit.name=my_persistence_unit)")
 * private JpaRepository repository;
 * </code>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(service = JpaRepository.class, name = JPA_FACTORY_PID, configurationPolicy = REQUIRE)
public class EclipseLinkRepository implements JpaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private EntityManagerFactory emf;

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T insert(T entity) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            em.persist(entity);
            txn.commit();
            return entity;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T update(T entity) {
        T updated;
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            updated = em.merge(entity);
            txn.commit();
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int updateByCriteria(UpdateCriteria<T> criteria) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> cu = cb.createCriteriaUpdate(criteria.getEntity());
            criteria.getUpdateAttributes().forEach(cu::set);
            Root<T> root = cu.from(criteria.getEntity());
            int rowsUpdated = em
                    .createQuery(cu.where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("No. of rows updated: {}", rowsUpdated);
            return rowsUpdated;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> void delete(Class<T> entity, Object primaryKey) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            T entityToDelete = em.find(entity, primaryKey);
            if (entityToDelete == null) {
                LOGGER.warn("Entity couldn't be deleted as it doesn't exists in DB: [{}]", entity);
            } else {
                em.remove(entityToDelete);
                txn.commit();
            }
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByJpaNamedQuery(CrudDTO<T> crudDTO) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            TypedQuery<T> typedQuery = em.createNamedQuery(crudDTO.getNamedQuery(), crudDTO.getEntity());
            int rowsDeleted = JpaUtil.setTypedQueryParams(typedQuery, crudDTO.getPosParams()).executeUpdate();
            txn.commit();
            LOGGER.debug("deleteByJpaNamedQuery: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteByCriteria(DeleteCriteria<T> criteria) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(criteria.getEntity());
            Root<T> root = cd.from(criteria.getEntity());
            int rowsDeleted = em
                    .createQuery(cd.where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .executeUpdate();
            txn.commit();
            LOGGER.debug("deleteByCriteria: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> int deleteAll(Class<T> entity) {
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            txn = JpaUtil.getTransaction(em);
            int rowsDeleted = em.createQuery(em.getCriteriaBuilder().createCriteriaDelete(entity)).executeUpdate();
            txn.commit();
            LOGGER.debug("deleteAll: No. of rows deleted: [{}]", rowsDeleted);
            return rowsDeleted;
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> T findById(Class<T> entity, Object primaryKey) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return em.find(entity, primaryKey);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<Tuple> findByTupleQuery(TupleQueryCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.multiselect(criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new))
                    .where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByCriteria(ReadCriteria<T> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(criteria.getEntity());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq
                    .where(cb.and(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root))))
                    .setFirstResult(criteria.getStartPos())
                    .setMaxResults(criteria.getMaxResult())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByJpaNamedQuery(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setTypedQueryParams(em.createNamedQuery(namedQuery, resultClass), posParams).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByNamedQuery(String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setQueryParams(em.createNamedQuery(namedQuery), posParams).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findAll(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity))).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecords(Class<T> entity, int startPos, int maxResult) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            return em.createQuery(cq.select(cq.from(entity)))
                    .setFirstResult(startPos)
                    .setMaxResults(maxResult)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            return JpaUtil.setTypedQueryParams(query, crudDTO.getPosParams()).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findPaginatedRecordsByJpaQuery(CrudDTO<T> crudDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            TypedQuery<T> typedQuery = em.createQuery(crudDTO.getJpaQuery(), crudDTO.getEntity());
            return JpaUtil.setTypedQueryParams(typedQuery, crudDTO.getPosParams())
                    .setFirstResult(crudDTO.getStartPos())
                    .setMaxResults(crudDTO.getMaxResult())
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> List<T> findByINOperator(Class<T> entity, String attributeName, List<Object> values) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entity);
            Root<T> root = cq.from(entity);
            return em.createQuery(cq
                    .select(root)
                    .where(root.get(attributeName).in(values)))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByQueryAndMapDefault(Class<T> resultClass, String nativeQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setQueryParams(em.createNativeQuery(nativeQuery, resultClass), posParams).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByQueryAndMapResultSet(Class<T> resultClass, ResultSetMappingDTO mappingDTO) {
        EntityManager em = this.emf.createEntityManager();
        try {
            Query query = em.createNativeQuery(mappingDTO.getNativeQuery(), mappingDTO.getResultSetMapping());
            return JpaUtil.setQueryParams(query, mappingDTO.getPosParams()).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByQueryAndMapConstructor(Class<T> resultClass, String jpaQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setTypedQueryParams(em.createQuery(jpaQuery, resultClass), posParams).getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity, C> List<C> findByCriteriaAndMapConstructor(ConstructorCriteria<T, C> criteria) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<C> cq = cb.createQuery(criteria.getConstructorClass());
            Root<T> root = cq.from(criteria.getEntity());
            return em.createQuery(cq.select(cb.construct(criteria.getConstructorClass(), criteria.getSelections()
                    .stream()
                    .map(root::get)
                    .toArray(Selection[]::new)))
                    .where(JpaUtil.getPredicates(criteria.getCriteriaAttributes(), cb, root)))
                    .getResultList();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultOfType(Class<T> resultClass, QueryType type, String query, List<Object> posParams) {
        T result = null;
        EntityManager em = this.emf.createEntityManager();
        try {
            switch (type) {
                case JPQL:
                    result = JpaUtil.setTypedQueryParams(em.createQuery(query, resultClass), posParams).getSingleResult();
                    break;
                case NATIVE:
                    result = resultClass.cast(JpaUtil.setQueryParams(em.createNativeQuery(query, resultClass), posParams)
                            .getSingleResult());
                    break;
            }
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getScalarResultOfType(Class<T> resultClass, String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setTypedQueryParams(em.createNamedQuery(namedQuery, resultClass), posParams).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    @Override
    public Object getScalarResult(String namedQuery, List<Object> posParams) {
        EntityManager em = this.emf.createEntityManager();
        try {
            return JpaUtil.setQueryParams(em.createNamedQuery(namedQuery), posParams).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Long count(Class<T> entity) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            return em.createQuery(cq.select(cb.count(cq.from(entity)))).getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntity> Long countByCriteria(Class<T> entity, Map<String, Object> criteriaAttributes) {
        EntityManager em = this.emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> from = cq.from(entity);
            return em.createQuery(cq.select(cb.count(from))
                    .where(cb.and(JpaUtil.getPredicates(criteriaAttributes, cb, from))))
                    .getSingleResult();
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(JpaCallback<T> action, boolean requiresTxn) {
        T result;
        EntityManager em = null;
        EntityTransaction txn = null;
        try {
            em = this.emf.createEntityManager();
            if (requiresTxn) {
                txn = JpaUtil.getTransaction(em);
                result = action.doInJpa(em);
                txn.commit();
            } else {
                result = action.doInJpa(em);
            }
        } catch (RuntimeException ex) {
            JpaUtil.setRollbackOnly(txn);
            LOGGER.error(ex.getMessage(), ex);
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            JpaUtil.rollbackTransaction(txn);
            JpaUtil.closeEntityManager(em);
        }
        return result;
    }

    // <----------------------------------------------- OSGi Internal ------------------------------------------------->

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        try {
            String unitName = config.osgi_unit_name();
            Validate.isTrue(StringUtils.isNotEmpty(unitName), "PersistenceUnit name can't be blank!!");
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", unitName);
            Map<String, Object> jpaProperties = JpaProperties.from(config);
            jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            jpaProperties.put(VALIDATION_MODE, config.validationMode());
            jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            this.emf = new PersistenceProvider().createEntityManagerFactory(unitName, jpaProperties);
            Validate.validState(this.emf != null, "Couldn't create EntityManagerFactory, most probably missing persistence.xml!!");
            LOGGER.info("Created EntityManagerFactory: [{}]", this.emf);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        LOGGER.info("Closing EntityManagerFactory for PersistenceUnit: [{}]", config.osgi_unit_name());
        try {
            this.emf.close();
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}

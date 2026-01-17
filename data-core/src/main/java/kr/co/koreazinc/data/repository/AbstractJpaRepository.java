package kr.co.koreazinc.data.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
public abstract class AbstractJpaRepository<T, K extends Serializable> {

    protected final EntityManager entityManager;

    protected final JpaEntityInformation<T, ?> entityInformation;

    protected final JPAQueryFactory queryFactory;

    private AbstractJpaRepository(Class<T> domainClass, final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    protected AbstractJpaRepository(Class<T> domainClass, final Collection<EntityManager> entityManagers) {
        this(domainClass, findContainEntityManager(domainClass, entityManagers));
    }

    private static EntityManager findContainEntityManager(Class<?> type, final Collection<EntityManager> entityManagers) {
        for (EntityManager entityManager : entityManagers) {
            Metamodel model = entityManager.getMetamodel();
            for (EntityType<?> entityType : model.getEntities()) {
                if (entityType.getJavaType().equals(type)) {
                    return entityManager;
                }
            }
        }
        log.error("not found EntityManager(Please check the PackagesToScan or make sure the bin has been initialized.)");
        return null;
    }

    public Class<T> getDomainClass() {
        return entityInformation.getJavaType();
    }

    public void flush() {
        entityManager.flush();
    }

    public T findOne(K key) {
        Assert.notNull(key, "The given key must not be null!");
        return entityManager.find(getDomainClass(), key);
    }

    public Collection<T> find() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());
        query.select(query.from(getDomainClass()));
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public <S extends T> S insert(S entity) {
        Assert.notNull(entity, "Entity must not be null.");
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    public <S extends T> Collection<S> insert(Iterable<S> entities) {
        Collection<S> result = new ArrayList<>();
        if (entities == null) return result;

        for (S entity : entities) {
            result.add(insert(entity));
        }
        return result;
    }

    @Transactional
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null.");
        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Transactional
    public <S extends T> Collection<S> save(Iterable<S> entities) {
        Collection<S> result = new ArrayList<>();
        if (entities == null) return result;

        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Transactional
    public <S extends T> void delete(S entity) {
        Assert.notNull(entity, "The entity must not be null!");
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Transactional
    public void delete(K key) {
        delete(findOne(key));
    }

    @Transactional
    public <S extends T> void delete(Iterable<S> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }
}
package kr.co.koreazinc.temp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class QuerydslConfig {

    @PersistenceContext(unitName = DataSourceConfig.DATABASE_NAME + "EntityManagerFactory")
    private EntityManager entityManager;

    @Bean(name = DataSourceConfig.DATABASE_NAME + "JpaQueryFactory")
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}

package kr.co.koreazinc.data.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Primary
    @Bean("jpaTransactionManager")
    @SuppressWarnings("deprecation")
    public PlatformTransactionManager jpaTransactionManager(@Autowired List<EntityManagerFactory> entityManagerFactories) {
        return new ChainedTransactionManager(
            entityManagerFactories.stream()
                .map(entityManagerFactory->new JpaTransactionManager(entityManagerFactory))
                .toArray(JpaTransactionManager[]::new)
        );
    }
}
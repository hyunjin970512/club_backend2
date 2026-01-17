package kr.co.koreazinc.temp.configuration;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "kr.co.koreazinc." + DataSourceConfig.DATABASE_NAME + ".repository",
        entityManagerFactoryRef = DataSourceConfig.DATABASE_NAME + "EntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager")
public class DataSourceConfig {

    // 데이터베이스 이름 설정
    public static final String DATABASE_NAME = "temp";

    // Entity 패키지 설정
    public static final List<String> PACKAGES = List.of("kr.co.koreazinc.data.model",
            String.format("kr.co.koreazinc.%s.model", DATABASE_NAME));

    @Primary
    @Bean(DATABASE_NAME + "DataSource")
    @ConfigurationProperties(prefix = DATABASE_NAME + ".datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(DATABASE_NAME + "JpaProperties")
    @ConfigurationProperties(prefix = DATABASE_NAME + ".jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean(DATABASE_NAME + "HibernateProperties")
    @ConfigurationProperties(prefix = DATABASE_NAME + ".jpa.hibernate")
    public HibernateProperties hibernateProperties() {
        return new HibernateProperties();
    }

    @Primary
    @Bean(DATABASE_NAME + "EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan(PACKAGES.toArray(String[]::new));
        entityManagerFactory.setPersistenceUnitName(DATABASE_NAME + "EntityManager");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(jpaProperties().getDatabasePlatform());
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = hibernateProperties().determineHibernateProperties(
                jpaProperties().getProperties(), new HibernateSettings());
        entityManagerFactory.setJpaPropertyMap(properties);
        return entityManagerFactory;
    }
}

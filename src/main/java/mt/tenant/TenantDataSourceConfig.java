package mt.tenant;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
public class TenantDataSourceConfig
{
    private static final String LIQUIBASE_AT = "liquibase-at";
    private static final String LIQUIBASE_HU = "liquibase-hu";

    @Bean
    @Qualifier("at")
    public DataSource dataSourceAt()
    {
        return dataSource("at");
    }

    @Bean
    @Qualifier("hu")
    public DataSource dataSourceHu()
    {
        return dataSource("hu");
    }

    @Bean(name = LIQUIBASE_HU)
    @Qualifier("at")
    public SpringLiquibase liquibaseAt()
    {
        return liquibase(dataSourceAt());
    }

    @Bean(name = LIQUIBASE_AT)
    @Qualifier("hu")
    public SpringLiquibase liquibaseHu()
    {
        return liquibase(dataSourceHu());
    }

    @Configuration
    protected static class LiquibaseJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor
    {
        public LiquibaseJpaDependencyConfiguration()
        {
            super(LIQUIBASE_AT, LIQUIBASE_HU);
        }
    }

    private static DataSource dataSource(String tenantIdentifier)
    {
        return DataSourceBuilder.create()
            .driverClassName("org.h2.Driver")
            .url("jdbc:h2:mem:" + tenantIdentifier)
            .username("sa")
            .build();
    }

    private static SpringLiquibase liquibase(DataSource dataSource)
    {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:/dbchange/master.xml");
        return liquibase;
    }

}

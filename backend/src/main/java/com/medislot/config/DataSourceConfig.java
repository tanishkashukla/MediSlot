package com.medislot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank() && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            try {
                log.info("Configuring PostgreSQL HikariDataSource from cloud DATABASE_URL...");
                // Handle postgres:// or postgresql://
                URI dbUri = new URI(databaseUrl.replace("postgresql://", "postgres://"));
                String username = "";
                String password = "";

                if (dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":", 2);
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }

                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String dbName = dbUri.getPath();
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbName;

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setIdleTimeout(30000);
                config.setMaxLifetime(600000);
                config.setConnectionTimeout(20000);

                log.info("Successfully configured PostgreSQL DataSource for {}", jdbcUrl);
                return new HikariDataSource(config);
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL, falling back to default DataSource properties", e);
            }
        }

        // Default local / standard Spring DataSource
        HikariConfig config = new HikariConfig();
        String url = env.getProperty("spring.datasource.url", "jdbc:h2:mem:medislotdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setJdbcUrl(url);
        config.setUsername(env.getProperty("spring.datasource.username", "sa"));
        config.setPassword(env.getProperty("spring.datasource.password", "password"));
        config.setDriverClassName(env.getProperty("spring.datasource.driverClassName", 
                env.getProperty("spring.datasource.driver-class-name", "org.h2.Driver")));
        return new HikariDataSource(config);
    }
}

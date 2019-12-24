package se.erikwelander.apis.configuration

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

const val BASE = "se.erikwelander.apis.api"

@Configuration
@ComponentScan(BASE)
@EntityScan(basePackages = [BASE+".models"])
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = [BASE+".repositories"])
internal class DataSourceConfiguration {

    @Bean
    fun persistenceExceptionTranslationPostProcessor() = PersistenceExceptionTranslationPostProcessor()

    @Bean
    fun hibernateJpaVendorAdapter() = HibernateJpaVendorAdapter()
}
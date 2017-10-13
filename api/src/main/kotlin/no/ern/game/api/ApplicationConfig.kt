package no.ern.game.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * game
 * NIK on 13/10/2017
 */
@Configuration
@EnableSwagger2
@EnableJpaRepositories(basePackages = arrayOf("no.ern.game.api"))
@EntityScan(basePackages = arrayOf("no.ern.game.api"))
class ApplicationConfig {
    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("API for card game")
                .description("Some description")
                .version("1.0")
                .build()
    }

    @Bean(name = arrayOf("OBJECT_MAPPER_BEAN"))
    fun jsonObjectMapper(): ObjectMapper {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                /*
                    JSON does not specify how dates should be represented, whereas JavaScript does.
                    And in JavaScript it is ISO 8601.
                    So, to represent dates to send over a network consumed by different clients,
                    it is reasonable to send them in ISO 8601 instead of a numeric timestamp.
                    Here we make sure timestamps are not used in marshalling of JSON data.
                    Example:
                    2001-01-05T13:15:30Z
                 */
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                //make sure we can use Java 8 dates
                .modules(JavaTimeModule())
                .build()
    }

    /*
    If you run this directly, you can then check the Swagger documentation at:
    http://localhost:8080/newsrest/api/swagger-ui.html
    */
}
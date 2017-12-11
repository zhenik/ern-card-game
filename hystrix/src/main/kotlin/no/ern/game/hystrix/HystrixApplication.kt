package no.ern.game.hystrix

import com.netflix.config.ConfigurationManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableSwagger2
class HystrixApplication {

    init {
        val conf = ConfigurationManager.getConfigInstance()
        conf.setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 500) //timeout time
        conf.setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 2) //How many fails before activate circuitbreak
        conf.setProperty("hystrix.command.default.circuitBreaker.errorThresholdPercentage", 50) //How high percent treshold for errors
        conf.setProperty("hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds", 10000) //How long circuit break stops requests
    }

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .build()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(HystrixApplication::class.java, *args)
}
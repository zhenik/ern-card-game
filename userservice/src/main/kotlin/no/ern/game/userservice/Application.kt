package no.ern.game.userservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class Application


fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
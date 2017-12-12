package no.ern.game.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaServerApplication

//Starts on port 6666, check localhost:6666 for status
fun main(args: Array<String>) {
    SpringApplication.run(EurekaServerApplication::class.java, *args)
}
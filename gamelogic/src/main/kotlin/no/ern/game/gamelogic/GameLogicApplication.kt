package no.ern.game.gamelogic

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.cloud.netflix.ribbon.RibbonClients
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@SpringBootApplication(scanBasePackages = arrayOf("no.ern.game.gamelogic"))
@EnableEurekaClient
@RibbonClients(
        RibbonClient (name = "item-server"),
        RibbonClient (name = "player-server"),
        RibbonClient (name = "match-server")
)
class GameLogicApplication : WebMvcConfigurerAdapter() {}

fun main(args: Array<String>) {
    SpringApplication.run(GameLogicApplication::class.java, *args)
}

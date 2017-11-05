package no.ern.game.gamelogic

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@SpringBootApplication(scanBasePackages = arrayOf("no.ern.game.gamelogic"))
class GameLogicApplication : WebMvcConfigurerAdapter() {}

fun main(args: Array<String>) {
    SpringApplication.run(GameLogicApplication::class.java, *args)
}

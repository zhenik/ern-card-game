package no.ern.game.item

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@SpringBootApplication(scanBasePackages = arrayOf("no.ern.game.item"))
class Application : WebMvcConfigurerAdapter() {}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

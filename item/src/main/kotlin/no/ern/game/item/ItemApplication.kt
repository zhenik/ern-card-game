package no.ern.game.item

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.cloud.netflix.ribbon.RibbonClient


@SpringBootApplication(scanBasePackages = arrayOf("no.ern.game.item"))
@EnableEurekaClient
@RibbonClient(name = "item-server")
class Application : WebMvcConfigurerAdapter() {}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

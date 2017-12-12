package no.ern.game.api

import no.ern.game.api.filter.JwtFilter
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
@EnableJpaRepositories(basePackages = arrayOf("no.ern.game.api"))
@EntityScan(basePackages = arrayOf("no.ern.game.api"))
class CorsConfig{

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true // you USUALLY want this
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("GET")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("PATCH")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun jwtFilter(): FilterRegistrationBean {
        val registrationBean = FilterRegistrationBean()
        registrationBean.filter = JwtFilter()
        registrationBean.addUrlPatterns("/auth/filter/*")
        return registrationBean
    }
}
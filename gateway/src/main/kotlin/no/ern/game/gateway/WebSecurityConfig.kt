package no.ern.game.gateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        private val dataSource: DataSource,
        private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {


    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .and()
                .logout()
                .and()
                //
                .authorizeRequests()
//                .antMatchers(HttpMethod.POST,"/entities").authenticated()
                .antMatchers("/gamelogic-server/**").authenticated()
                .antMatchers(HttpMethod.GET,"/entities").permitAll()
                .antMatchers(HttpMethod.GET,"/player-server/**").authenticated()
                .antMatchers(HttpMethod.GET,"/item-server/**").authenticated()
                .antMatchers(HttpMethod.GET,"/match-server/**").authenticated()
                .antMatchers("/user").authenticated()
                .antMatchers("/signIn").permitAll()
                .anyRequest().denyAll()
                .and()
                /*
                    CSRF would be on by default.
                    Here we configure it.
                    The CSRF token can be sent in different ways.
                    Here we send it as a cookie readable from JS.
                    This is secure, and greatly simplify the handling
                    of it in the browser.
                 */
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    }


    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("""
                     SELECT username, password, enabled
                     FROM users
                     WHERE username=?
                     """)
                .authoritiesByUsernameQuery("""
                     SELECT x.username, y.roles
                     FROM users x, user_entity_roles y
                     WHERE x.username=? and y.user_entity_username=x.username
                     """)
                .passwordEncoder(passwordEncoder)

//        "SELECT x.username, y.roles FROM users x, user_entity_roles y WHERE x.username=? and y.user_entity_username=x.username")
    }
}
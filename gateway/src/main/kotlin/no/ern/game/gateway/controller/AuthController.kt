package no.ern.game.gateway.controller

import no.ern.game.gateway.service.AmqpService
import no.ern.game.gateway.service.UserService
import no.ern.game.schema.dto.PlayerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@Validated
class AuthController(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
){
    @Autowired
    private lateinit var amqpService: AmqpService

    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String,Any>()
        map.put("name", user.name)
        map.put("roles", AuthorityUtils.authorityListToSet((user as Authentication).authorities))
        return ResponseEntity.ok(map)
    }

    @PostMapping(path = arrayOf("/signIn"),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun signIn(@ModelAttribute(name = "the_user") username: String,
               @ModelAttribute(name = "the_password") password: String)
            : ResponseEntity<Void> {

        val registered = service.createUserWithHashedPassword(username, password, setOf("USER"))

        if (!registered) {
            return ResponseEntity.status(400).build()
        }
        else {
            try {
                val playerDto = PlayerDto(
                        username,
                        null,
                        100,
                        5,
                        20,
                        20,
                        1,
                        setOf()
                )
                amqpService.sendPlayer(playerDto)
            } catch (e: Exception) {
                println("!!! IT'S FUCKED UP !!!")
            }
        }

        val userDetails = userDetailsService.loadUserByUsername(username)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).build()
    }
}
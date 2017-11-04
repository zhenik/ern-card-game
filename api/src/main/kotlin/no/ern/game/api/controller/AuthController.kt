package no.ern.game.api.controller

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.swagger.annotations.Api
import no.ern.game.api.domain.dto.EntityDto
import no.ern.game.api.domain.model.User
import no.ern.game.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

const val SECRET_KEY = "secretkey"

@Api(value = "/auth", description = "API for authentication.")
@RequestMapping(
        path = arrayOf("/auth"),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
)
@RestController
@Validated
class AuthController{

    @Autowired
    private lateinit var crud: UserRepository

    @RequestMapping(value = "/signup", method = arrayOf(RequestMethod.POST))
    @Throws(Exception::class)
    fun signup(@RequestBody json: Map<String, String>): Long? {
        var id: Long? = null

        try {
            val name = json["username"]
            val password = json["password"]
            println(name + " : " + password)
            val user = User(name!!, password!!)
            crud.save(user)
            id = user.id
        } catch (e: NumberFormatException) {
            throw Exception("smth happened")
        }
        return id
    }

    @RequestMapping(value = "/login", method = arrayOf(RequestMethod.POST))
    @Throws(Exception::class)
    fun login(@RequestBody json: Map<String, String>): ResponseEntity<String> {
        var token: String?
        try {
            val name = json["username"]
            val password = json["password"]

            val user = crud.findUserByUsername(name!!)
            if (password == user.password!!) {
                token = Jwts.builder()
                        .setSubject(name.toString())
                        .claim("roles", "user")
                        .setIssuedAt(Date()).signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                        .compact()
            } else {
//                throw Exception("Invalid input")
                return ResponseEntity.status(400).build()

            }
        } catch (e: NumberFormatException) {
//            throw Exception("smth happened")
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.ok(token)
    }

    @RequestMapping(value = "/filter/me", method = arrayOf(RequestMethod.GET))
    @Throws(Exception::class)
    fun me(): String {
        return "Cors policy works"
    }
}
package no.ern.game.gateway.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ern.game.gateway.domain.model.UserConverter
import no.ern.game.gateway.domain.model.UserDto
import no.ern.game.gateway.repository.UserRepository
import no.ern.game.gateway.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Api(value = "/users", description = "API for user entities")
@RequestMapping(
        path = arrayOf("/users"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class PlayerController {

    @Autowired
    private lateinit var repo: UserRepository

    @Autowired
    private lateinit var userService: UserService


    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun createPlayer(
            @ApiParam("UserEntity to save")
            @RequestBody
            userDto: UserDto
    ): ResponseEntity<Long> {

        if (userDto.id!=null || userDto.password!!.isNullOrEmpty() || userDto.username!!.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        if (repo.findUserByUsername(userDto.username!!) != null) {
            return ResponseEntity.status(409).build()
        }

        userService.createUserWithHashedPassword(userDto.username!!.toLowerCase(), userDto.password!!)

        // Username must be unique
        return ResponseEntity.status(201).build()
    }



    @ApiOperation("Fetch all users")
    @GetMapping
    fun getAllPlayers(): ResponseEntity<Iterable<UserDto>> {
        return ResponseEntity.ok(UserConverter.transform(repo.findAll()))
    }

}

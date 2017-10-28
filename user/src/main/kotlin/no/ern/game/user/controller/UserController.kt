package no.ern.game.user.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ern.game.user.domain.converters.UserConverter
import no.ern.game.user.domain.dto.UserDto
import no.ern.game.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

@Api(value = "/", description = "API for user entities")
@RequestMapping(
        path = arrayOf("/"),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
)
@RestController
@Validated
class UserController {

    @Autowired
    private lateinit var repo: UserRepository

    @ApiOperation("Get all users by level")
    @GetMapping
    fun getAllUsersByLevel(
            @ApiParam("Level to find")
            @RequestParam(name = "level", required = false)
            level: Int?
    ): ResponseEntity<Iterable<UserDto>> {
        if (level == null) {
            return ResponseEntity.ok(UserConverter.transform(repo.findAll()))
        }

        if(level < 1) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.ok(UserConverter.transform(repo.findAllByLevel(level)))
    }

    @ApiOperation("Create new user")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun createUser(
            @ApiParam("User to save")
            @RequestBody
            userDto: UserDto): ResponseEntity<Long> {

        if (!userDto.id.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        try {
            val savedId = repo.createUser(
                    username = userDto.username!!,
                    password = userDto.password!!,
                    salt = userDto.salt!!,
                    health = userDto.health!!,
                    damage = userDto.damage!!,
                    avatar = userDto.avatar!!,
                    currency = userDto.currency!!,
                    experience = userDto.experience!!,
                    level = userDto.level!!,
                    equipment = userDto.equipment!!
            )

            return ResponseEntity.status(201).body(savedId)

        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }
    }
}
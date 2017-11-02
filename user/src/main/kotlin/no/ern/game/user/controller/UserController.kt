package no.ern.game.user.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.user.domain.converters.UserConverter
import no.ern.game.user.domain.dto.UserDto
import no.ern.game.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

@Api(value = "/users", description = "API for user entities")
@RequestMapping(
        path = arrayOf("/users"),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
)
@RestController
@Validated
class UserController {

    @Autowired
    private lateinit var repo: UserRepository

    @ApiOperation("Create new user")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(code = 201, message = "Id of created user")
    fun createUser(
            @ApiParam("User to save")
            @RequestBody
            userDto: UserDto): ResponseEntity<Long> {

        if (!userDto.id.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        if (!isDtoValid(userDto)) {
            return ResponseEntity.status(400).build()
        }

        // Checks for null i isDtoValid method.
        if(repo.existsByUsername(userDto.username!!)) {
            return ResponseEntity.status(400).build()
        }

        try {
            val savedId = repo.createUser(
                    username = userDto.username!!,
                    password = userDto.password!!,
                    salt = userDto.salt!!,
                    health = userDto.health!!,
                    damage = userDto.damage!!,
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

        if (level < 1) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.ok(UserConverter.transform(repo.findAllByLevel(level)))
    }

    @ApiOperation("Get user by username")
    @GetMapping(path = arrayOf("/{username}"))
    fun getUserByUsername(
            @ApiParam("Username to search by")
            @PathVariable("username")
            username: String?
    ): ResponseEntity<UserDto> {
        return if (username.isNullOrBlank()) {
            ResponseEntity.status(404).build()
        } else {
            // username cannot be null because we already checked.
            ResponseEntity.ok(UserConverter.transform(repo.findFirstByUsername(username!!)))
        }
    }


    @ApiOperation("Replace the data of a user")
    @PutMapping(path = arrayOf("/{username}"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun updateUserByUsername(
            @ApiParam("Username defining the user. Id or username cannot be changed")
            @PathVariable("username")
            username: String?,

            @ApiParam("Data to replace old user")
            @RequestBody
            userDto: UserDto
    ): ResponseEntity<Long> {
        if (username.isNullOrBlank()) {
            return ResponseEntity.status(400).build()
        }
        // Dont change ID or username.
        if (username != userDto.username || userDto.id != null) {
            return ResponseEntity.status(400).build()
        }
        if (!repo.existsByUsername(username!!)) {
            return ResponseEntity.status(404).build()
        }
        if (!isDtoValid(userDto)) {
            return ResponseEntity.status(400).build()
        }

        try {
            val user = repo.findFirstByUsername(username)

            user.password = userDto.password!!
            user.salt =  userDto.salt!!
            user.health =  userDto.health!!
            user.damage =  userDto.damage!!
            user.currency =  userDto.currency!!
            user.experience =  userDto.experience!!
            user.level =  userDto.level!!
            user.equipment = userDto.equipment!!

            repo.save(user)
            return ResponseEntity.status(204).build()
        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }
    }

    @ApiOperation("Delete a user by username")
    @DeleteMapping(path = arrayOf("/{username}"))
    fun deleteUserByUsername(
            @ApiParam("Username of user to delete")
            @PathVariable("username")
            username: String?
    ) : ResponseEntity<Any>{

        if (username.isNullOrBlank()) {
            return ResponseEntity.status(400).build()
        }
        if (!repo.existsByUsername(username!!)) {
            return ResponseEntity.status(404).build()
        }
        repo.deleteByUsername(username)
        return ResponseEntity.status(204).build()
    }

    private fun isDtoValid(userDto: UserDto): Boolean {
        if ((!userDto.username.isNullOrBlank()) &&
                (!userDto.password.isNullOrBlank()) &&
                userDto.salt != null &&
                userDto.health != null &&
                userDto.damage != null &&
                userDto.currency != null &&
                userDto.experience != null
                ) {
            return true
        }
        return false
    }
}
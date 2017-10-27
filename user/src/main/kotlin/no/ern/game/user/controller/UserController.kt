package no.ern.game.user.controller

import io.swagger.annotations.ApiOperation
import no.ern.game.user.domain.converters.UserConverter
import no.ern.game.user.domain.dto.UserDto
import no.ern.game.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class UserController {

    @Autowired
    private lateinit var repo: UserRepository

    @ApiOperation("Get all users")
    @GetMapping
    fun getAllUsers(): ResponseEntity<UserDto> {
        return ResponseEntity.ok(UserConverter.transform(repo.findAll()))
    }
}
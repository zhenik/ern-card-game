package no.ern.game.player.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.player.domain.converters.PlayerConverter
import no.ern.game.player.domain.model.PlayerDto
import no.ern.game.player.repository.PlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

@Api(value = "/players", description = "API for player entities")
@RequestMapping(
        path = arrayOf("/players"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class PlayerController {

    @Autowired
    private lateinit var repo: PlayerRepository

    @ApiOperation("Create new player")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 201, message = "Id of created player")
    fun createPlayer(
            @ApiParam("Player to save")
            @RequestBody
            playerDto: PlayerDto): ResponseEntity<Long> {

        if (!playerDto.id.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()
        }

        if (!isDtoFieldsNotNull(playerDto)) {
            return ResponseEntity.status(400).build()
        }

        // Checks for null i isDtoFieldsNotNull method.
        if(repo.existsByUsername(playerDto.username!!)) {
            return ResponseEntity.status(400).build()
        }

        try {
            val savedId = repo.createPlayer(
                    username = playerDto.username!!,
                    password = playerDto.password!!,
                    salt = playerDto.salt!!,
                    health = playerDto.health!!,
                    damage = playerDto.damage!!,
                    currency = playerDto.currency!!,
                    experience = playerDto.experience!!,
                    level = playerDto.level!!,
                    equipment = playerDto.equipment!!
            )

            return ResponseEntity.status(201).body(savedId)

        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }
    }

    @ApiOperation("Get all players by level")
    @GetMapping
    fun getAllPlayersByLevel(
            @ApiParam("Level to find")
            @RequestParam(name = "level", required = false)
            level: Int?
    ): ResponseEntity<Iterable<PlayerDto>> {
        if (level == null) {
            return ResponseEntity.ok(PlayerConverter.transform(repo.findAll()))
        }

        if (level < 1) {
            return ResponseEntity.status(400).build()
        }

        return ResponseEntity.ok(PlayerConverter.transform(repo.findAllByLevel(level)))
    }

    @ApiOperation("Get player by username")
    @GetMapping(path = arrayOf("/{username}"))
    fun getPlayerByUsername(
            @ApiParam("Username to search by")
            @PathVariable("username")
            username: String?
    ): ResponseEntity<PlayerDto> {
        if (username.isNullOrBlank()) {
            return ResponseEntity.status(404).build()
        } else {
            // username cannot be null because we already checked.
            val entity = repo.findFirstByUsername(username!!)
            if (entity == null) {
                return ResponseEntity.status(404).build()
            }
            return ResponseEntity.ok(PlayerConverter.transform(entity))
        }
    }


    @ApiOperation("Replace the data of a player")
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun updatePlayer(
            @ApiParam("Id defining the player.")
            @PathVariable("id")
            id: String?,

            @ApiParam("Data to replace old player. Id cannot be changed, and must be the same in path and RequestBody")
            @RequestBody
            playerDto: PlayerDto
    ): ResponseEntity<Long> {
        val dtoId: Long
        try {
            dtoId = playerDto.id!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(404).build()
        }

        // Don't change ID
        if (playerDto.id != id) {
            return ResponseEntity.status(409).build()
        }
        if (!repo.exists(dtoId)) {
            return ResponseEntity.status(404).build()
        }
        if (!isDtoFieldsNotNull(playerDto)) {
            return ResponseEntity.status(400).build()
        }

        try {
            val successful = repo.updatePlayer(playerDto.username!!,
                    playerDto.password!!,
                    playerDto.health!!,
                    playerDto.damage!!,
                    playerDto.currency!!,
                    playerDto.experience!!,
                    playerDto.level!!,
                    playerDto.equipment!!,
                    playerDto.id!!.toLong()
            )
            if (!successful) {
                return ResponseEntity.status(400).build()
            }
            return ResponseEntity.status(204).build()
        } catch (e: ConstraintViolationException) {
            return ResponseEntity.status(400).build()
        }
    }

    @ApiOperation("Replace the username of a player")
    @PatchMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateUsername(
            @ApiParam("Id defining the player.")
            @PathVariable("id")
            id: Long,

            @ApiParam("New username for player. Username must be unique and must be a string.")
            @RequestBody
            username: String
    ): ResponseEntity<Long> {
        if (!repo.exists(id)) {
            return ResponseEntity.status(404).build()
        }

        if (username.isNullOrEmpty() || username.length > 50) {
            return ResponseEntity.status(400).build()
        }
        if (!repo.setUsername(username, id)) {
            return ResponseEntity.status(400).build()
        }
        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Delete a player by username")
    @DeleteMapping(path = arrayOf("/{username}"))
    fun deleteUserByUsername(
            @ApiParam("Username of player to delete")
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

    private fun isDtoFieldsNotNull(playerDto: PlayerDto): Boolean {
        if ((!playerDto.username.isNullOrBlank()) &&
                (!playerDto.password.isNullOrBlank()) &&
                playerDto.salt != null &&
                playerDto.health != null &&
                playerDto.damage != null &&
                playerDto.currency != null &&
                playerDto.experience != null
                ) {
            return true
        }
        return false
    }
}
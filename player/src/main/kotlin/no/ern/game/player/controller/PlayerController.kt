package no.ern.game.player.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.player.domain.converters.PlayerConverter
import no.ern.game.player.repository.PlayerRepository
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.PlayerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
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

        try {
            val savedId = repo.createPlayer(
                    userId = playerDto.userId!!.toLong(),
                    username = playerDto.username!!,
                    health = playerDto.health!!,
                    damage = playerDto.damage!!,
                    currency = playerDto.currency!!,
                    experience = playerDto.experience!!,
                    level = playerDto.level!!,
                    items = mutableSetOf()
            )
            return ResponseEntity.status(201).body(savedId)

        } catch (e: java.lang.Exception) {
            return ResponseEntity.status(400).build()
        }
    }

    @ApiOperation("Adds item to player")
    @PostMapping(path = arrayOf("/{id}/items"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun addItemToPlayer(
            @PathVariable("id")
            id: Long,
            @RequestBody
            itemDto: ItemDto): ResponseEntity<Void> {

        //TODO test with Wiremock
        var rest: RestTemplate = RestTemplateBuilder().build()
        val player = repo.findOne(id)

        // check if entity exists
        val itemURL = "http://localhost:8083/game/api/items/" + itemDto.id
        val response: ResponseEntity<ItemDto> = try {
            rest.getForEntity(itemURL, ItemDto::class.java)
        } catch (e: HttpClientErrorException) {
            return ResponseEntity.status(400).build()
        }

        if (itemDto.id == null || player == null || response.statusCodeValue != 200) {
            return ResponseEntity.status(400).build()
        }


        if (repo.addItem(id, itemDto.id!!.toLong())) {
            return ResponseEntity.status(200).build()
        } else {
            return ResponseEntity.status(400).build()
        }

    }


    @ApiOperation("Get player specified by id")
    @GetMapping(path = arrayOf("/{id}"))
    fun getPlayerById(@ApiParam("Id of Player")
                      @PathVariable("id")
                      pathId: String?)
            : ResponseEntity<PlayerDto> {
        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        val dto = repo.findOne(id) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(PlayerConverter.transform(dto))
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

    @ApiOperation("Replace the data of a player")
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun updatePlayer(
            @ApiParam("Id defining the player.")
            @PathVariable("id")
            pathId: String?,

            @ApiParam("Data to replace old player. Id cannot be changed, and must be the same in path and RequestBody")
            @RequestBody
            playerDto: PlayerDto
    ): ResponseEntity<Long> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(404).build()
        }

        // Don't change ID
        if (playerDto.id != pathId) {
            return ResponseEntity.status(409).build()
        }
        if (!repo.exists(id)) {
            return ResponseEntity.status(404).build()
        }
        if (!isDtoFieldsNotNull(playerDto)) {
            return ResponseEntity.status(400).build()
        }

        try {
            val successful = repo.updatePlayer(
                    playerDto.username!!,
                    playerDto.health!!,
                    playerDto.damage!!,
                    playerDto.currency!!,
                    playerDto.experience!!,
                    playerDto.level!!,
                    playerDto.items!!.toMutableSet(),
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

    @ApiOperation("Replace the currency of a player")
    @PatchMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateCurrency(
            @ApiParam("Id defining the player.")
            @PathVariable("id")
            id: Long,

            @ApiParam("New currency for player. Currency cannot be negative.")
            @RequestBody
            currency: String
    ): ResponseEntity<Void> {

        if (!repo.exists(id)) {
            return ResponseEntity.status(404).build()
        }
        val newCurrency = try {
            currency.toInt()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }
        if (newCurrency < 0) {
            return ResponseEntity.status(400).build()
        }
        if (!repo.setCurrency(newCurrency, id)) {
            return ResponseEntity.status(400).build()
        }
        return ResponseEntity.status(204).build()
    }


    @ApiOperation("Delete user by id")
    @DeleteMapping(path = arrayOf("/{id}"))
    fun deleteUserById(
            @ApiParam("Id of user to delete")
            @PathVariable("id")
            pathId: Long?
    ): ResponseEntity<Any> {

        val id: Long
        try {
            id = pathId!!.toLong()
            repo.delete(id)
        } catch (e: NumberFormatException) {
            return ResponseEntity.status(400).build()
        } catch (e1: java.lang.Exception) {
            return ResponseEntity.status(404).build()
        }

        return ResponseEntity.status(204).build()
    }

    private fun isDtoFieldsNotNull(playerDto: PlayerDto): Boolean {
        if (playerDto.username.isNullOrBlank()) {
            return false
        }
        if (playerDto.health != null &&
                playerDto.damage != null &&
                playerDto.currency != null &&
                playerDto.experience != null &&
                playerDto.level != null &&
                playerDto.userId != null
                ) {
            return true
        }
        return false
    }
}
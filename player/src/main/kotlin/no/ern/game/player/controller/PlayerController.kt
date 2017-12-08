//package no.ern.game.player.controller
//
//import io.swagger.annotations.Api
//import io.swagger.annotations.ApiOperation
//import io.swagger.annotations.ApiParam
//import io.swagger.annotations.ApiResponse
//import no.ern.game.player.domain.converters.PlayerConverter
//import no.ern.game.player.repository.PlayerRepository
//import no.ern.game.schema.dto.PlayerDto
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.MediaType
//import org.springframework.http.ResponseEntity
//import org.springframework.validation.annotation.Validated
//import org.springframework.web.bind.annotation.*
//import javax.validation.ConstraintViolationException
//
//@Api(value = "/players", description = "API for player entities")
//@RequestMapping(
//        path = arrayOf("/players"),
//        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
//)
//@RestController
//@Validated
//class PlayerController {
//
//    @Autowired
//    private lateinit var repo: PlayerRepository
//
//    @ApiOperation("Create new player")
//    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
//    @ApiResponse(code = 201, message = "Id of created player")
//    fun createPlayer(
//            @ApiParam("Player to save")
//            @RequestBody
//            playerDto: PlayerDto): ResponseEntity<Void> {
//
//        if (playerDto.id.isNullOrEmpty()) {
//            return ResponseEntity.status(400).build()
//        }
//
//        if (!isDtoFieldsNotNull(playerDto)) {
//            return ResponseEntity.status(400).build()
//        }
//
//        try {
//            repo.createPlayer(
//                    health = playerDto.health!!,
//                    damage = playerDto.damage!!,
//                    currency = playerDto.currency!!,
//                    experience = playerDto.experience!!,
//                    level = playerDto.level!!,
//                    items = playerDto.items!!.toMutableList(),
//                    id = playerDto.id!!.toLong()
//            )
//            return ResponseEntity.status(201).build()
//
//        } catch (e: ConstraintViolationException) {
//            return ResponseEntity.status(400).build()
//        }
//    }
//
//    @ApiOperation("Get player specified by id")
//    @GetMapping(path = arrayOf("/{id}"))
//    fun getPlayerById(@ApiParam("Id of Player")
//                      @PathVariable("id")
//                      pathId: String?)
//            : ResponseEntity<PlayerDto> {
//
//        val id: Long
//        try {
//            id = pathId!!.toLong()
//        } catch (e: Exception) {
//            return ResponseEntity.status(400).build()
//        }
//
//        val dto = repo.findOne(id) ?: return ResponseEntity.status(404).build()
//        return ResponseEntity.ok(PlayerConverter.transform(dto))
//    }
//
//    @ApiOperation("Get all players by level")
//    @GetMapping
//    fun getAllPlayersByLevel(
//            @ApiParam("Level to find")
//            @RequestParam(name = "level", required = false)
//            level: Int?
//    ): ResponseEntity<Iterable<PlayerDto>> {
//        if (level == null) {
//            return ResponseEntity.ok(PlayerConverter.transform(repo.findAll()))
//        }
//
//        if (level < 1) {
//            return ResponseEntity.status(400).build()
//        }
//
//        return ResponseEntity.ok(PlayerConverter.transform(repo.findAllByLevel(level)))
//    }
//
//    @ApiOperation("Replace the data of a player")
//    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
//    fun updatePlayer(
//            @ApiParam("Id defining the player.")
//            @PathVariable("id")
//            id: String?,
//
//            @ApiParam("Data to replace old player. Id cannot be changed, and must be the same in path and RequestBody")
//            @RequestBody
//            playerDto: PlayerDto
//    ): ResponseEntity<Long> {
//        val dtoId: Long
//        try {
//            dtoId = playerDto.id!!.toLong()
//        } catch (e: Exception) {
//            return ResponseEntity.status(404).build()
//        }
//
//        // Don't change ID
//        if (playerDto.id != id) {
//            return ResponseEntity.status(409).build()
//        }
//        if (!repo.exists(dtoId)) {
//            return ResponseEntity.status(404).build()
//        }
//        if (!isDtoFieldsNotNull(playerDto)) {
//            return ResponseEntity.status(400).build()
//        }
//
//        try {
//            val successful = repo.updatePlayer(
//                    playerDto.health!!,
//                    playerDto.damage!!,
//                    playerDto.currency!!,
//                    playerDto.experience!!,
//                    playerDto.level!!,
//                    playerDto.items!!.toMutableList(),
//                    playerDto.id!!.toLong()
//            )
//            if (!successful) {
//                return ResponseEntity.status(400).build()
//            }
//            return ResponseEntity.status(204).build()
//        } catch (e: ConstraintViolationException) {
//            return ResponseEntity.status(400).build()
//        }
//    }
//
//    @ApiOperation("Replace the currency of a player")
//    @PatchMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
//    fun updateCurrency(
//            @ApiParam("Id defining the player.")
//            @PathVariable("id")
//            id: Long,
//
//            @ApiParam("New currency for player. Currency cannot be negative.")
//            @RequestBody
//            currency: Int
//    ): ResponseEntity<Long> {
//
//        if (!repo.exists(id)) {
//            return ResponseEntity.status(404).build()
//        }
//        if (currency < 0) {
//            return ResponseEntity.status(400).build()
//        }
//        if (!repo.setCurrency(currency, id)) {
//            return ResponseEntity.status(400).build()
//        }
//        return ResponseEntity.status(204).build()
//    }
//
//
//    @ApiOperation("Delete user by id")
//    @DeleteMapping(path = arrayOf("/{id}"))
//    fun deleteUserById(
//            @ApiParam("Id of user to delete")
//            @PathVariable("id")
//            pathId: Long?
//    ): ResponseEntity<Any> {
//
//        val id: Long
//        try {
//            id = pathId!!.toLong()
//        } catch (e: Exception) {
//            return ResponseEntity.status(400).build()
//        }
//        repo.delete(id)
//        return ResponseEntity.status(204).build()
//    }
//
//    private fun isDtoFieldsNotNull(playerDto: PlayerDto): Boolean {
//        if (playerDto.health != null &&
//                playerDto.damage != null &&
//                playerDto.currency != null &&
//                playerDto.experience != null &&
//                playerDto.level != null
//                ) {
//            return true
//        }
//        return false
//    }
//}
package no.ern.game.gamelogic.controller

import io.swagger.annotations.*
import no.ern.game.gamelogic.domain.converters.PlayerFightConverter
import no.ern.game.gamelogic.domain.converters.PlayerSearchConverter
import no.ern.game.gamelogic.domain.model.Character
import no.ern.game.gamelogic.services.AmqpService
import no.ern.game.gamelogic.services.GameProcessorService
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerResultDto
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.schema.dto.gamelogic.FightResultLogDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto

import org.springframework.security.core.Authentication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.util.*




@Api(value = "/play", description = "API for game logic processes.")
@RequestMapping(
        path = arrayOf("/play")
)
@RestController
@Validated
class GameLogicController {

    @Autowired
    lateinit var restTemplate : RestTemplate

    @Autowired
    lateinit var gameService: GameProcessorService

    @Autowired
    lateinit var amqpService : AmqpService

    @Value("\${playerServerName}")
    private lateinit var playersPath: String
    @Value("\${itemServerName}")
    private lateinit var itemsPath: String



    @ApiOperation("Find opponent (random enemy)")
    @ApiResponses(
            ApiResponse(code = 200, message = "The opponent found"),
            ApiResponse(code = 404, message = "No opponent found")
    )
    @GetMapping(path = arrayOf("/enemy"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun findEnemy(authentication: Authentication) : ResponseEntity<PlayerSearchDto>? {

        // 1 make request to player module.
        val response : ResponseEntity<Array<PlayerDto>> = try {
            val url = "$playersPath/players"

            restTemplate.getForEntity(url, Array<PlayerDto>::class.java)
        } catch (e: HttpClientErrorException) {
            return ResponseEntity.status(e.statusCode.value()).build()
        }

        // 3 get list. If list is empty return bad request
        val players = response.body.asList()
        if (players.isEmpty()){
            return ResponseEntity.status(404).build()
        }


        val callerUsername = authentication.name
        val playersFiltered = excludeFromListByUsername(players,callerUsername)


        // 4 get random from list
        if(playersFiltered.isNotEmpty()){
            return ResponseEntity.ok(PlayerSearchConverter.transform(playersFiltered[Random().nextInt(playersFiltered.size)]))
        } else {
            return ResponseEntity.status(404).build()
        }
    }

    @ApiOperation("Initiate fight")
    @PostMapping(path = arrayOf("/fight"),consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponses(
            ApiResponse(code = 200, message = "The fight is over and returns match-log, represented as FightResultLogDto"),
            ApiResponse(code = 404, message = "Opponent(s) not found"),
            ApiResponse(code = 400, message = "Given payload is invalid, check request body (Example: trying fight with yourself)")
    )
    fun startFight(
            authentication: Authentication,
            @ApiParam("Model represent ids of users who are going to fight against each other")
            @RequestBody playerSearchDto: PlayerSearchDto
    ) : ResponseEntity<FightResultLogDto> {



        if(!isPlayersFightDtoIdValid(playerSearchDto)){

            return ResponseEntity.status(400).build()
        }

        val callerUsername = authentication.name

        /** 2 fetch players*/
        var attackerPlayerDto : PlayerDto
        var defenderPlayerDto : PlayerDto
        try{

            val urlAttacker = "$playersPath/players?username=${authentication.name}"
            val urlDefender = "$playersPath/players/${playerSearchDto.id!!.toLong()}"

            val responseAttacker: ResponseEntity<Array<PlayerDto>> = restTemplate.getForEntity(urlAttacker, Array<PlayerDto>::class.java)

            if(responseAttacker.body.toList().size != 1){

                return ResponseEntity.status(400).build()
            } else {
                attackerPlayerDto = responseAttacker.body.toList().first()
            }

            val responseDefender : ResponseEntity<PlayerDto> = restTemplate.getForEntity(urlDefender, PlayerDto::class.java)


            // player(s) not found
            if (responseAttacker.statusCodeValue!=200 || responseDefender.statusCodeValue!=200){

                return ResponseEntity.status(404).build()
            }


            defenderPlayerDto = responseDefender.body

            // 0 validate this attacker initiate request (possible only with Security)
            if( callerUsername.toLowerCase() != (attackerPlayerDto.username!!.toLowerCase()) ) {

                return ResponseEntity.status(400).build()
            }

            // validate that caller cant fight himself
            if(callerUsername.toLowerCase() == defenderPlayerDto.username!!.toLowerCase()){
                return ResponseEntity.status(400).build()
            }


        }
        catch (e: HttpClientErrorException){
            return ResponseEntity.status(e.rawStatusCode).build()
        }


        /** 3 fetch players items */
        var attackerItemsDto: List<ItemDto> = listOf()
        var defenderItemsDto: List<ItemDto> = listOf()
        // 3.1 attacker items (divided for efficiency)
        if (playerHasItems(attackerPlayerDto)){
            try {
                val query = queryItemBuilder(attackerPlayerDto.items!!)
                val urlAttackerItem = "$itemsPath/items?ids=$query"
                val responseAttackerItems: ResponseEntity<Array<ItemDto>> = restTemplate.getForEntity(urlAttackerItem, Array<ItemDto>::class.java)
                attackerItemsDto = responseAttackerItems.body.toList()
            }
            catch (e: HttpClientErrorException){
                return ResponseEntity.status(e.rawStatusCode).build()
            }
        }
        // 3.2 attacker items (divided for efficiency)
        if (playerHasItems(defenderPlayerDto)){
            try {
                val query = queryItemBuilder(defenderPlayerDto.items!!)
                val urlDefenderItem = "$itemsPath/items?ids=$query"
                val responseDefenderItems: ResponseEntity<Array<ItemDto>> = restTemplate.getForEntity(urlDefenderItem, Array<ItemDto>::class.java)
                defenderItemsDto = responseDefenderItems.body.toList()
            }
            catch (e: HttpClientErrorException){
                return ResponseEntity.status(e.rawStatusCode).build()
            }
        }



        /** 4 call GameProcessorService.fight(attacker,defender) -> return GameLogDto */
        val attacker: Character = PlayerFightConverter.transform(attackerPlayerDto,attackerItemsDto)
        val defender: Character = PlayerFightConverter.transform(defenderPlayerDto,defenderItemsDto)

        val fightResultGameLog = gameService.fight(attacker,defender)

        /** 5 send matchResult to MatchResult processor rabbitMQ send */
        // In try catch because be able run local wireMock tests
        val matchResult = getMatchResult(attacker,defender,fightResultGameLog.winner!!)
        try {
            amqpService.sendMatchResultCreated(matchResult)
        }
        catch (e: Exception){}

        /** 6 Improvements todo: generate experience for player(s) and persist it (if use rabbitMq || persist directly via HTTP) */

        /** 7 return FightResultLogDto */
        return ResponseEntity.ok(fightResultGameLog)
    }

    fun queryItemBuilder(list: Collection<Long>):String {
        var line  = ""
        val builder = StringBuilder()
        if (list.isNotEmpty()) {
            list.forEach({
                builder.append("$it,")
            })
            line = builder.toString()
            line = line.substring(0, line.length - 1)
        }
        return line
    }

    fun playerHasItems(player: PlayerDto): Boolean {
        if (player.items != null){
            if(player.items!!.isNotEmpty()){
                return true
            }
        }
        return false
    }



    private fun isPlayersFightDtoIdValid(dto: PlayerSearchDto):Boolean{
        try {
            val id = dto.id!!.toLong()
            return true
        }catch (e: Exception){}
        return false
    }



    private fun getMatchResult(attacker: Character, defender: Character, winner: String):MatchResultDto{
        return MatchResultDto(
                PlayerResultDto(
                        attacker.playerId,
                        attacker.username,
                        attacker.health,
                        attacker.damage,
                        attacker.remainingHealth),
                PlayerResultDto(
                        defender.playerId,
                        defender.username,
                        defender.health,
                        defender.damage,
                        defender.remainingHealth
                ),
                winner
        )
    }

}


fun excludeFromListByUsername(players: List<PlayerDto>, username: String): MutableList<PlayerDto> {
    val result = mutableListOf<PlayerDto>()
    players
        .filter { it.username != null }
        .filter { it.username!!.toLowerCase() != username.toLowerCase()}
        .toCollection(result)

    return result
}
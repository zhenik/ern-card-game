package no.ern.game.gamelogic.controller

import io.swagger.annotations.*
import no.ern.game.gamelogic.domain.converters.PlayerFightConverter
import no.ern.game.gamelogic.domain.converters.PlayerSearchConverter
import no.ern.game.gamelogic.domain.model.Character
import no.ern.game.gamelogic.services.GameProcessorService
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerResultDto
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.schema.dto.gamelogic.FightResultLogDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import no.ern.game.schema.dto.gamelogic.PlayersFightIdsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*

@Api(value = "/play", description = "API for game logic processes.")
@RequestMapping(
        path = arrayOf("/play")
//        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class GameLogicController {

    @Autowired
    lateinit var restTemplate : RestTemplate

    @Autowired
    lateinit var gameService: GameProcessorService

    @Value("\${playerServerName}")
    private lateinit var playersPath: String
    @Value("\${itemServerName}")
    private lateinit var itemsPath: String
    @Value("\${matchServerName}")
    private lateinit var matchesPath: String

    @ApiOperation("""
        Find opponent, which is closest to hunter level (+/- 1 level).
        If level not defined, find opponent in limit from 0 to 2 level""")
    @ApiResponses(
            ApiResponse(code = 200, message = "The opponent found"),
            ApiResponse(code = 404, message = "No opponent found")
    )
    @GetMapping(path = arrayOf("/enemy"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun findEnemy() : ResponseEntity<PlayerSearchDto>? {


        // 1 make request to player module.
        val response : ResponseEntity<Array<PlayerDto>> = try {
            val url = "$playersPath/game/api/players"
            restTemplate.getForEntity(url, Array<PlayerDto>::class.java)
        } catch (e: HttpClientErrorException) {
            return ResponseEntity.status(e.statusCode.value()).build()
        }

        // 3 get list. If list is empty return bad request
        val players = response.body.asList()
        if (players.isEmpty()){
            return ResponseEntity.status(404).build()
        }

        // 3.1 TODO: make list filter => to exclude /me when Security works
//        val playersFiltered = players.filter { searchLevel-1<=it.level!!.toInt() && it.level!!.toInt()<=searchLevel+1 }
        val playersFiltered = players

        // 4 get random from list
        if(playersFiltered.isNotEmpty()){
            return ResponseEntity.ok(PlayerSearchConverter.transform(playersFiltered[Random().nextInt(playersFiltered.size)]))
        } else {
            return ResponseEntity.status(404).build()
        }
    }

//    @ApiOperation("Initiate match")
//    @PostMapping(path = arrayOf("/fight"),consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
//    @ApiResponse(code = 200, message = "The fight log of match, represents as FightResultLogDto")
//    fun startFight(
//            @ApiParam("Model represent ids of users who are going to fight against each other")
//            @RequestBody resultIdsDto: PlayersFightIdsDto) : ResponseEntity<FightResultLogDto>{
//
//        // 1 validate PlayersFightIdsDto
//        if( ! isPlayersFightIdsDtoValid(resultIdsDto)){ return ResponseEntity.status(400).build() }
//
//        // 2.1 TODO: fetch users from user-module (if any errors -> propagate them). Extract logic to method
//        val attackerUserDtoMock = PlayerDto("1","attackerName",null,null,100,10,null,null,1,null)
//        val defenderUserDtoMock = PlayerDto("2","defenderName",null,null,120,12,null,null,2,null)
//
//        // 2.2 TODO: fetch their items (validate lvl requirements for each item). Extract logic to method
//        val randomItems1: List<ItemDto> = getMockListOfItems()
//        val randomItems2: List<ItemDto> = getMockListOfItems()
//
//        // 3 call GameProcessorService.fight(attacker,defender) -> return GameLogDto
//        val attacker: Character = PlayerFightConverter.transform(attackerUserDtoMock,randomItems1)
//        val defender: Character = PlayerFightConverter.transform(defenderUserDtoMock,randomItems2)
//
//
//        val fightResultGameLog = gameService.fight(attacker,defender)
//
//        // 4 send matchResult to MatchResult processor (if use rabbitMq || persist directly via HTTP)
//        val matchResult = getMatchResult(attacker,defender,fightResultGameLog.winner!!)
//        val responseMatchApi : ResponseEntity<Long> = restTemplate.postForEntity(matchesPath,matchResult,Long::class.java)
//        if (responseMatchApi.statusCode.value()!=201){ return ResponseEntity.status(responseMatchApi.statusCode.value()).build() }
//
//
//        // 5 TODO: generate experience for player(s) and persist it (if use rabbitMq || persist directly via HTTP)
//        // How to rollback if this fail (? delete on responseMatch ?)
//
//        // 6 return FightResultLogDto
//        return ResponseEntity.ok(fightResultGameLog)
//
//    }


    // TODO: remove later mock items
    private fun getMockListOfItems():List<ItemDto>{
        // 1 weapon
        // 1 armor
        return listOf(
                ItemDto(null,null,null,(Math.random()*15).toInt(),0),
                ItemDto(null,null,null,0,(Math.random()*15).toInt())
                )
    }

    private fun isPlayersFightIdsDtoValid(dto: PlayersFightIdsDto):Boolean{
        if(dto.attackerId.isNullOrBlank() || dto.defenderId.isNullOrBlank()) return false
        if(dto.attackerId!!.trim()==dto.defenderId!!.trim()) return false
        return true
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
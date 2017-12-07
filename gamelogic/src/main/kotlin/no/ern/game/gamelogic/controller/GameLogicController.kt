package no.ern.game.gamelogic.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.gamelogic.domain.converters.PlayerFightConverter
import no.ern.game.gamelogic.domain.converters.PlayerSearchConverter
import no.ern.game.gamelogic.domain.model.Player
import no.ern.game.gamelogic.services.GameProcessorService
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.schema.dto.UserDto
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

    @Value("\${gameApis.user.path}")
    private lateinit var usersPath: String
    @Value("\${gameApis.item.path}")
    private lateinit var itemsPath: String
    @Value("\${gameApis.match.path}")
    private lateinit var matchesPath: String

    //TODO: remove after wiremock tests
    @ApiOperation("Health check")
    @GetMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun check() : ResponseEntity<String>{
        return ResponseEntity.ok("UP")
    }

    //TODO: remove after wiremock tests
    @GetMapping(path = arrayOf("/chain"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun chainCheck() : ResponseEntity<String>{

        val newPath = matchesPath+"/string"
        println(newPath)

        val response  = try {
            restTemplate.getForEntity(newPath, String::class.java)
        } catch (e: HttpClientErrorException){ }
        return ResponseEntity.ok(response.toString())
    }

    @ApiOperation("""
        Find opponent, which is closest to hunter level (+/- 1 level).
        If level not defined, find opponent in limit from 0 to 2 level""")
    @GetMapping(path = arrayOf("/hunting"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 200, message = "The opponent found")
    fun findEnemy(
            @ApiParam("Search enemy for specific lvl")
            @RequestParam("level", required = false)
            level: Int?
    ) : ResponseEntity<PlayerSearchDto>? {

        // ur level, by default or level in query. ?level=1
        var searchLevel : Int = 1
        if(level!=null){
            searchLevel=level
        }

        /** 1 make request to user module.
            (specific of restTemplate)
            @see package org.tsdes.spring.rest.wiremock.ConverterRestServiceXml)
        */
        val response : ResponseEntity<Array<UserDto>> = try {
            restTemplate.getForEntity(usersPath, Array<UserDto>::class.java)
        } catch (e: HttpClientErrorException){
            val code = if (e.statusCode.value() == 400) 400 else 500
            return ResponseEntity.status(code).build()
        }

            // 2 Additional Error handling
        if (response.statusCode.run{ is4xxClientError || is5xxServerError}){
            val code = if (response.statusCode.value() == 400) 400 else 500
            return ResponseEntity.status(code).build()
        }

            // 3 get list. If list is empty return bad request
            // (TODO:think what to return)
        val players = response.body.asList()
        if (players.isEmpty()){ return ResponseEntity.status(404).build() }


            // 3.1 (TODO: make list filter => to exclude /me)
            // 3.2 (filter list by lvl in limit between [level-1,level+1] )
        val playersFiltered = players.filter { searchLevel-1<=it.level!!.toInt() && it.level!!.toInt()<=searchLevel+1 }

            // 4 get random from list
        if(playersFiltered.isNotEmpty()){
            return ResponseEntity.ok(PlayerSearchConverter.transform(playersFiltered[Random().nextInt(playersFiltered.size)]))
        } else {
            return ResponseEntity.ok(PlayerSearchConverter.transform(players[Random().nextInt(players.size)]))
        }
    }

    @ApiOperation("Initiate match")
    @PostMapping(path = arrayOf("/fight"),consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 200, message = "The fight log of match, represents as FightResultLogDto")
    fun startFight(
            @ApiParam("Model represent ids of users who are going to fight against each other")
            @RequestBody resultIdsDto: PlayersFightIdsDto) : ResponseEntity<FightResultLogDto>{

        // 1 validate PlayersFightIdsDto
        if( ! isPlayersFightIdsDtoValid(resultIdsDto)){ return ResponseEntity.status(400).build() }

        // 2.1 TODO: fetch users from user-module (if any errors -> propagate them). Extract logic to method
        val attackerUserDtoMock = UserDto("1","attackerName",null,null,100,10,null,null,1,null)
        val defenderUserDtoMock = UserDto("2","defenderName",null,null,120,12,null,null,2,null)

        // 2.2 TODO: fetch their items (validate lvl requirements for each item). Extract logic to method
        val randomItems1: List<ItemDto> = getMockListOfItems()
        val randomItems2: List<ItemDto> = getMockListOfItems()

        // 3 call GameProcessorService.fight(attacker,defender) -> return GameLogDto
        val attacker: Player = PlayerFightConverter.transform(attackerUserDtoMock,randomItems1)
        val defender: Player = PlayerFightConverter.transform(defenderUserDtoMock,randomItems2)


        val fightResultGameLog = gameService.fight(attacker,defender)

        // 4 send matchResult to MatchResult processor (if use rabbitMq || persist directly via HTTP)
        val matchResult = getMatchResult(attacker,defender,fightResultGameLog.winner!!)
        val responseMatchApi : ResponseEntity<Long> = restTemplate.postForEntity(matchesPath,matchResult,Long::class.java)
        if (responseMatchApi.statusCode.value()!=201){ return ResponseEntity.status(responseMatchApi.statusCode.value()).build() }


        // 5 TODO: generate experience for player(s) and persist it (if use rabbitMq || persist directly via HTTP)
        // How to rollback if this fail (? delete on responseMatch ?)

        // 6 return FightResultLogDto
        return ResponseEntity.ok(fightResultGameLog)

    }


    // TODO: remove later mock items
    private fun getMockListOfItems():List<ItemDto>{
        // 1 weapon
        // 1 armor
        return listOf(
                ItemDto(null,null,null,(Math.random()*15).toLong(),0),
                ItemDto(null,null,null,0,(Math.random()*15).toLong())
                )
    }

    private fun isPlayersFightIdsDtoValid(dto: PlayersFightIdsDto):Boolean{
        if(dto.attackerId.isNullOrBlank() || dto.defenderId.isNullOrBlank()) return false
        if(dto.attackerId!!.trim()==dto.defenderId!!.trim()) return false
        return true
    }

    private fun getMatchResult(attacker: Player, defender:Player, winner: String):MatchResultDto{
        return MatchResultDto(
                PlayerDto(
                        attacker.username,
                        attacker.health,
                        attacker.damage,
                        attacker.remainingHealth),
                PlayerDto(
                        defender.username,
                        defender.health,
                        defender.damage,
                        defender.remainingHealth
                ),
                winner
        )
    }

}
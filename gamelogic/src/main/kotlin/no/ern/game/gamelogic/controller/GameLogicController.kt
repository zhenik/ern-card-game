package no.ern.game.gamelogic.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.gamelogic.domain.converters.PlayerFightConverter
import no.ern.game.gamelogic.domain.converters.PlayerSearchConverter
import no.ern.game.gamelogic.domain.model.Player
import no.ern.game.gamelogic.services.GameProcessService
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.UserDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import no.ern.game.schema.dto.gamelogic.PlayersFightDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*

@Api(value = "/play", description = "API for game logic processes.")
@RequestMapping(
        path = arrayOf("/play"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class GameLogicController {

    @Autowired
    lateinit var restTemplate : RestTemplate

    @Autowired
    lateinit var gameService: GameProcessService

    @Value("\${gameApis.user.path}")
    private lateinit var usersPath: String

    @ApiOperation("Find opponent")
    @GetMapping(path = arrayOf("/hunting"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
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

        /** 1
         *  make request to user module. Find single player, which is closest to my searchLevel.
         *  if level not defined, find any user close to 1st level
         * */

            // 1 Request
        val response : ResponseEntity<Array<UserDto>> =
                restTemplate.getForEntity(usersPath, Array<UserDto>::class.java)

            // 2 Error handling
        if (response.statusCode.value()!=200){ return ResponseEntity.status(response.statusCode.value()).build() }

            // 3 get list. If list is empty return nothing (TODO:think what to return)
        val players = response.body.asList()
        if (players.isEmpty()){ return ResponseEntity.status(200).build() }


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
    @ApiResponse(code = 200, message = "The log of match")
    fun startFight(
            @ApiParam("Model represent ids of users who are going to fight against each other")
            @RequestBody resultDto: PlayersFightDto) : ResponseEntity<Long>{

        // 1 TODO: validate PlayersFightDto
        // 2.1 TODO: fetch users from user-module (if any errors -> propagate them)
        // 2.2 TODO: fetch their items (validate lvl requirements for each item)
        // 3 call GameProcessService.fight(attacker,defender) -> return GameLogDto
        // 4 TODO: send matchResult to MatchResult processor (if use rabbitMq || persist directly via HTTP)
        // 5 TODO: generate experience for player(s) and persist it
        // 6 TODO: return GameLogDto





        //2.1 Users
        val attackerUserDtoMock = UserDto("1","attackerName",null,null,100,10,null,null,1,null)
        val defenderUserDtoMock = UserDto("2","defenderName",null,null,120,12,null,null,2,null)
        //2.2 Items
        val randomItems1: List<ItemDto> = getMockListOfItems()
        val randomItems2: List<ItemDto> = getMockListOfItems()

        val attacker: Player = PlayerFightConverter.transform(attackerUserDtoMock,randomItems1)
        val defender: Player = PlayerFightConverter.transform(defenderUserDtoMock,randomItems2)


        //3 TODO: return FightResultLogDto
        gameService.fight(attacker,defender)

        return ResponseEntity.ok(1L)

    }


    private fun getMockListOfItems():List<ItemDto>{
        // 1 weapon
        // 1 armor
        return listOf(
                ItemDto(null,null,null,(Math.random()*15).toLong(),0),
                ItemDto(null,null,null,0,(Math.random()*15).toLong())
                )
    }

}
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
import java.lang.Exception
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

    @ApiOperation("Initiate match")
    @PostMapping(path = arrayOf("/fight"),consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 200, message = "The fight log of match, represents as FightResultLogDto")
    fun startFight(
            @ApiParam("Model represent ids of users who are going to fight against each other")
            @RequestBody resultIdsDto: PlayersFightIdsDto
    ) : ResponseEntity<FightResultLogDto>{

        // 0 TODO: validate this attacker initiate that call (possible only with Security)

        // 1 validate that all fields are present (PlayersFightIdsDto) and ids are Long and ids are different
        if( ! isPlayersFightIdsDtoValid(resultIdsDto)) {
            return ResponseEntity.status(400).build()
        }

        /** 2 fetch players*/
        var attackerPlayerDto : PlayerDto
        var defenderPlayerDto : PlayerDto
        try{
            val urlAttacker = "$playersPath/game/api/players/${resultIdsDto.attackerId!!.toLong()}"
            val urlDefender = "$playersPath/game/api/players/${resultIdsDto.defenderId!!.toLong()}"
            val responseAttacker : ResponseEntity<PlayerDto> = restTemplate.getForEntity(urlAttacker, PlayerDto::class.java)
            val responseDefender : ResponseEntity<PlayerDto> = restTemplate.getForEntity(urlDefender, PlayerDto::class.java)

            // player(s) not found
            if (responseAttacker.statusCodeValue!=200 || responseDefender.statusCodeValue!=200){
                return ResponseEntity.status(404).build()
            }

            attackerPlayerDto = responseAttacker.body
            defenderPlayerDto = responseDefender.body
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
                val urlAttackerItem = "$itemsPath/game/api/items?ids=$query"
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
                val urlDefenderItem = "$itemsPath/game/api/items?ids=$query"
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
//
        // 5 send matchResult to MatchResult processor (if use rabbitMq || persist directly via HTTP)
        val matchResult = getMatchResult(attacker,defender,fightResultGameLog.winner!!)
        val matchUrl = "$matchesPath/game/api/matches"
        val responseMatchApi : ResponseEntity<Long> = restTemplate.postForEntity(matchUrl,matchResult,Long::class.java)
        if (responseMatchApi.statusCode.value()!=201){
            return ResponseEntity.status(responseMatchApi.statusCode.value()).build()
        }

        // 6 Improvements todo: generate experience for player(s) and persist it (if use rabbitMq || persist directly via HTTP)
        // How to rollback if this fail (? delete on responseMatch ?)

        // 7 return FightResultLogDto
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
            line = builder.toString().substring(0, line.length - 1)
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


    // TODO: remove later mock items
    private fun getMockListOfItems():List<ItemDto>{
        // 1 weapon
        // 1 armor
        return listOf(
                ItemDto(null,null,null,(Math.random()*15).toInt(),0),
                ItemDto(null,null,null,0,(Math.random()*15).toInt())
                )
    }

    // TODO: test it
    private fun isPlayersFightIdsDtoValid(dto: PlayersFightIdsDto):Boolean{
        try {
            val attackerId = dto.attackerId!!.toLong()
            val defenderId = dto.defenderId!!.toLong()
            if (attackerId!=defenderId){
                return true
            }
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
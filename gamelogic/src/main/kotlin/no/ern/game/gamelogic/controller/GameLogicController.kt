package no.ern.game.gamelogic.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ern.game.gamelogic.domain.converters.PlayerSearchConverter
import no.ern.game.schema.dto.UserDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
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

    @Value("\${gameApis.user.path}")
    private lateinit var usersPath: String

    @ApiOperation("Find opponent")
    @GetMapping(path = arrayOf("/hunting"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun createMatchResult(
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

}


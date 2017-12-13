package no.ern.game.userservice


import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import no.ern.game.userservice.domain.PlayerSearchConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*


@RestController
class RestApi {

    @Value("\${playerServerName}")
    private lateinit var playersPath: String
    @Value("\${itemServerName}")
    private lateinit var itemsPath: String

    @Autowired
    lateinit var restTemplate : RestTemplate


    /**
     * Get the number of existing users
     */
    @GetMapping(path = arrayOf("/usersInfoCount"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(7)
    }

    /*
        Note: for simplicity here using Entity as DTO...
     */

    @GetMapping(path = arrayOf("/username"))
    fun currentUserName(authentication: Authentication): String {
        return authentication.name
    }

    @ApiOperation("""
        Find opponent, which is closest to hunter level (+/- 1 level).
        If level not defined, find opponent in limit from 0 to 2 level""")
    @ApiResponses(
            ApiResponse(code = 200, message = "The opponent found"),
            ApiResponse(code = 404, message = "No opponent found")
    )
    @GetMapping(path = arrayOf("/enemy"), produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun findEnemy(authentication: Authentication) : ResponseEntity<PlayerSearchDto>? {

        println("ITS HERE ----------> "+authentication.name)

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
}



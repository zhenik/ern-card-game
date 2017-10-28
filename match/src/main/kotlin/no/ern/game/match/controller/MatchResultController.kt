package no.ern.game.match.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.match.domain.converters.MatchResultConverter
import no.ern.game.match.domain.dto.MatchResultDto
import no.ern.game.match.repository.MatchResultRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
    TODO: check status code
 */
@Api(value = "/", description = "API for match results.")
@RequestMapping(
        path = arrayOf("/"),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
)
@RestController
@Validated
class MatchResultController {

    @Autowired
    private lateinit var crud: MatchResultRepository

    @ApiOperation("Retrieve all match results. Fetch all match results by default. Fetch all match results for specific user if username provided in parameter")
    @GetMapping
    fun getEntities(
            @ApiParam("The specific username as parameter")
            @RequestParam("username", required = false) username: String?
    ) : ResponseEntity<List<MatchResultDto>> {
        if(username==null)
            return ResponseEntity.ok(MatchResultConverter.transform(crud.findAll()))
        return ResponseEntity.ok(MatchResultConverter.transform(crud.getMatchesByUserName(username)))
    }


    @ApiOperation("Create an entity")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(code = 201, message = "The id of newly entity")
    fun createEntity(
            @ApiParam("The question and answer options")
            @RequestBody resultDto: MatchResultDto) : ResponseEntity<Long>{


        if (!validDto(resultDto)){
            return ResponseEntity.status(400).build()
        }

        val id = registerMatch(resultDto)

        // check if persisted
        if (id==-1L)
            return ResponseEntity.status(400).build()

        return ResponseEntity.status(201).body(id)
    }

    fun validDto(resultDto: MatchResultDto): Boolean{
        if (
        resultDto.username1!=null &&
                resultDto.username2!=null &&
                resultDto.totalDamage1!=null &&
                resultDto.totalDamage2!=null &&
                resultDto.remainingHealth1!=null &&
                resultDto.remainingHealth2!= null &&
                resultDto.winnerName!=null)
            return true
        return false
    }

    fun registerMatch(resultDto: MatchResultDto): Long{
        return crud.createMatch(
                resultDto.username1!!,
                resultDto.username2!!,
                resultDto.totalDamage1!!,
                resultDto.totalDamage2!!,
                resultDto.remainingHealth1!!,
                resultDto.remainingHealth2!!,
                resultDto.winnerName!!)
    }
}
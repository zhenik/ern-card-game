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
import javax.validation.ConstraintViolationException

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

    @ApiOperation("Retrieve all match results. Fetch all match results by default. Fetch all match results for specific user if username provided in request parameters")
    @GetMapping
    fun getMatchesResults(
            @ApiParam("The specific username as parameter")
            @RequestParam("username", required = false) username: String?
    ) : ResponseEntity<List<MatchResultDto>> {
        if(username==null)
            return ResponseEntity.ok(MatchResultConverter.transform(crud.findAll()) as List<MatchResultDto>)
        return ResponseEntity.ok(MatchResultConverter.transform(crud.getMatchesByUserName(username)) as List<MatchResultDto>)
    }


    @ApiOperation("Create a match result")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(code = 201, message = "The id of newly match result")
    fun createMatchResult(
            @ApiParam("The match result model")
            @RequestBody resultDto: MatchResultDto) : ResponseEntity<Long>{

        if (!validDto(resultDto)){
            return ResponseEntity.status(400).build()
        }

        try{
            val id = registerMatch(resultDto)
            return ResponseEntity.status(201).body(id)
        }catch (e: ConstraintViolationException){
            // 422 Unprocessable Entity
            // 409 Conflict (for duplication id)
            return ResponseEntity.status(422).build()
        }catch (e: Exception){
            return ResponseEntity.status(500).build()
        }

    }

    fun validDto(resultDto: MatchResultDto): Boolean{
        if (
        resultDto.attackerUsername!=null &&
                resultDto.defenderUsername!=null &&
                resultDto.attackerHealth!=null &&
                resultDto.defenderHealth!=null &&
                resultDto.attackerTotalDamage!=null &&
                resultDto.defenderTotalDamage!= null &&
                resultDto.attackerRemainingHealth!= null &&
                resultDto.defenderRemainingHealth!= null &&
                resultDto.winnerName!=null)
            return true
        return false
    }

    fun registerMatch(resultDto: MatchResultDto): Long{
        return crud.createMatchResult(
                resultDto.attackerUsername!!,
                resultDto.defenderUsername!!,
                resultDto.attackerHealth!!,
                resultDto.defenderHealth!!,
                resultDto.attackerTotalDamage!!,
                resultDto.defenderTotalDamage!!,
                resultDto.attackerRemainingHealth!!,
                resultDto.defenderRemainingHealth!!,
                resultDto.winnerName!!)
    }
}
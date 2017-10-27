package no.ern.game.match.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.ern.game.match.domain.converters.MatchConverter
import no.ern.game.match.domain.dto.MatchDto
import no.ern.game.match.repository.MatchRepository
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
class MatchController{

    @Autowired
    private lateinit var crud: MatchRepository

    @ApiOperation("Retrieve all match results. Fetch all match results by default. Fetch all match results for specific user if username provided in parameter")
    @GetMapping
    fun getEntities(
            @ApiParam("The specific username as parameter")
            @RequestParam("username", required = false) username: String?
    ) : ResponseEntity<List<MatchDto>> {
        if(username==null)
            return ResponseEntity.ok(MatchConverter.transform(crud.findAll()))
        return ResponseEntity.ok(MatchConverter.transform(crud.getMatchesByUserName(username)))
    }


    @ApiOperation("Create an entity")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(code = 201, message = "The id of newly entity")
    fun createEntity(
            @ApiParam("The question and answer options")
            @RequestBody dto: MatchDto) : ResponseEntity<Long>{


        if (!validDto(dto)){
            return ResponseEntity.status(400).build()
        }

        val id = registerMatch(dto)

        // check if persisted
        if (id==-1L)
            return ResponseEntity.status(400).build()

        return ResponseEntity.status(201).body(id)
    }

    fun validDto(dto: MatchDto): Boolean{
        if (
        dto.username1!=null &&
                dto.username2!=null &&
                dto.totalDamage1!=null &&
                dto.totalDamage2!=null &&
                dto.remainingHealth1!=null &&
                dto.remainingHealth2!= null &&
                dto.winnerName!=null)
            return true
        return false
    }

    fun registerMatch(dto: MatchDto): Long{
        return crud.createMatch(
                dto.username1!!,
                dto.username2!!,
                dto.totalDamage1!!,
                dto.totalDamage2!!,
                dto.remainingHealth1!!,
                dto.remainingHealth2!!,
                dto.winnerName!!)
    }
}
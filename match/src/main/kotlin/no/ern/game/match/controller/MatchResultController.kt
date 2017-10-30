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

const val ID_PARAM = "The numeric id of the match result"

//TODO: PUT, PATCH

@Api(value = "/matches", description = "API for match results.")
@RequestMapping(
        path = arrayOf("/matches"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class MatchResultController {

    @Autowired
    private lateinit var crud: MatchResultRepository

    @ApiOperation("Retrieve all match results. Fetch all match results by default. Fetch all match results for specific user if username provided in request parameters")
    @GetMapping
    fun getMatchesResults(@ApiParam("The specific username as parameter")
                          @RequestParam("username", required = false)
                          username: String?
    ): ResponseEntity<List<MatchResultDto>> {

        when(username.isNullOrBlank()){
            true ->
                return ResponseEntity.ok(MatchResultConverter.transform(crud.findAll()) as List<MatchResultDto>)
            false ->
                return ResponseEntity.ok(MatchResultConverter.transform(crud.getMatchesByUserName(username!!)) as List<MatchResultDto>)
        }
    }


    @ApiOperation("Create a match result")
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
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

    @ApiOperation("Get a single match result specified by id")
    @GetMapping(path = arrayOf("/{id}"))
    fun getMatchResult(@ApiParam(ID_PARAM)
                @PathVariable("id")
                pathId: String?)
            : ResponseEntity<MatchResultDto> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        val dto = crud.findOne(id) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(MatchResultConverter.transform(dto))
    }

    @ApiOperation("Delete a match result entity with the given id")
    @DeleteMapping(path = arrayOf("/{id}"))
    fun delete(@ApiParam(ID_PARAM)
               @PathVariable("id")
               pathId: String?): ResponseEntity<Any> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (!crud.exists(id)) {
            return ResponseEntity.status(404).build()
        }
        crud.delete(id)
        return ResponseEntity.status(204).build()
    }

    fun validDto(resultDto: MatchResultDto): Boolean{

        if (
        resultDto.attacker?.username!=null &&
                resultDto.defender?.username!=null &&
                resultDto.attacker?.health!=null &&
                resultDto.defender?.health!=null &&
                resultDto.attacker?.damage!=null &&
                resultDto.defender?.damage!= null &&
                resultDto.attacker?.remainingHealth!= null &&
                resultDto.defender?.remainingHealth!= null &&
                resultDto.winnerName!=null &&
                // check if id is present than false
                resultDto.id==null)
            return true
        return false
    }

    fun registerMatch(resultDto: MatchResultDto): Long{
        return crud.createMatchResult(
                resultDto.attacker!!.username!!,
                resultDto.defender!!.username!!,
                resultDto.attacker!!.health!!,
                resultDto.defender!!.health!!,
                resultDto.attacker!!.damage!!,
                resultDto.defender!!.damage!!,
                resultDto.attacker!!.remainingHealth!!,
                resultDto.defender!!.remainingHealth!!,
                resultDto.winnerName!!)
    }
}
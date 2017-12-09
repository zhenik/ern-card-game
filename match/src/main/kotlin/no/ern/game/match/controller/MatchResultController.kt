package no.ern.game.match.controller

import io.swagger.annotations.*
import no.ern.game.match.domain.converters.MatchResultConverter
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.match.repository.MatchResultRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

const val ID_PARAM = "The numeric id of the match result"

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

    @ApiOperation("Fetch all match results by default or with specific username if provided in request parameters")
    @ApiResponse(code = 200, message = "List of match results")
    @GetMapping
    fun getMatchesResults(
            @ApiParam("The specific username as parameter")
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
    @ApiResponses(
            ApiResponse(code = 201, message = "Match result created, return id of new resource"),
            ApiResponse(code = 409, message = "Conflict: given dto's properties does not follow constraints"),
            ApiResponse(code = 400, message = "Given dto does not have required properties")
    )
    fun createMatchResult(
            @ApiParam("The match result model")
            @RequestBody resultDto: MatchResultDto
    ) : ResponseEntity<Long> {

        if (!validDto(resultDto)){
            return ResponseEntity.status(400).build()
        }

        try {
            val id = registerMatch(resultDto)
            return ResponseEntity.status(201).body(id)
        }
        catch (e: ConstraintViolationException){
            // 409 Conflict (for duplication id)
            return ResponseEntity.status(409).build()
        }
        catch (e: Exception){
            return ResponseEntity.status(400).build()
        }

    }

    @ApiOperation("Get a single match result specified by id")
    @ApiResponses(
            ApiResponse(code = 400, message = "Given path param is invalid, can not be parsed to long"),
            ApiResponse(code = 404, message = "Match result with given id not found"),
            ApiResponse(code = 200, message = "Return match result with given id")
    )
    @GetMapping(path = arrayOf("/{id}"))
    fun getMatchResult(
            @ApiParam(ID_PARAM)
            @PathVariable("id") pathId: Long
    ) : ResponseEntity<MatchResultDto> {
//        val id: Long
//        try {
//            id = pathId!!.toLong()
//        } catch (e: Exception) {
//            return ResponseEntity.status(400).build()
//        }
        val dto = crud.findOne(pathId) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(MatchResultConverter.transform(dto))
    }

    @ApiOperation("Delete a match result entity with the given id")
    @ApiResponses(
            ApiResponse(code = 400, message = "Given path param is invalid, can not be parsed to long"),
            ApiResponse(code = 404, message = "Match result with given id not found"),
            ApiResponse(code = 204, message = "Match result with given id was deleted")
    )
    @DeleteMapping(path = arrayOf("/{id}"))
    fun delete(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            pathId: Long
    ): ResponseEntity<Any> {

        if (!crud.exists(pathId)) {
            return ResponseEntity.status(404).build()
        }
        crud.delete(pathId)
        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Update an existing matchResult")
    @ApiResponses(
            ApiResponse(code = 400, message = "Given path param is invalid, can not be parsed to long or updating fail while processing"),
            ApiResponse(code = 409, message = "Conflict: given dto's id is not the same that in path parameter"),
            ApiResponse(code = 404, message = "Match result with given path parameter id not found"),
            ApiResponse(code = 204, message = "Match result with given id was deleted")
    )
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun update(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            pathId: Long,
            @ApiParam("The matchResult that will replace the old one. Cannot change its id though.")
            @RequestBody
            dto: MatchResultDto
    ): ResponseEntity<Any> {
        val dtoId: Long

        try {
            dtoId = dto.id!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (dtoId != pathId) {
            return ResponseEntity.status(409).build()
        }

        if (!crud.exists(dtoId)) {
            return ResponseEntity.status(404).build()
        }

        if(!updateMatch(dto))
            return ResponseEntity.status(400).build()

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Modify the winner name of given matchResult Id")
    @ApiResponses(
            ApiResponse(code = 404, message = "Match result not found"),
            ApiResponse(code = 400, message = "Updating process fail while processing"),
            ApiResponse(code = 204, message = "Winner name in match result, with given id, was updated")
    )
    @PatchMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateWinnerName(
            @ApiParam("The unique id of the MatchResult")
            @PathVariable("id")
            id: Long,
            @ApiParam("""
                  The instructions on how to modify the winnername.
                  In this specific matchResult, it should be a single string value
              """)
              @RequestBody
              winnerName: String
    ) : ResponseEntity<Void> {

        // not exist
        if (!crud.exists(id)) {
            return ResponseEntity.status(404).build()
        }

        // not valid winnerName
        if(!crud.changeWinnerName(id,winnerName)){
            return ResponseEntity.status(400).build()
        } else {
            return ResponseEntity.status(204).build()
        }
    }

    fun validDto(resultDto: MatchResultDto): Boolean{
        try {
            resultDto.attacker!!.id!!.toLong()
            resultDto.defender!!.id!!.toLong()
        }
        catch (e: Exception){
            return false
        }

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
        { return true }

        return false
    }

    fun registerMatch(resultDto: MatchResultDto): Long{
        return crud.createMatchResult(
                resultDto.attacker!!.id!!.toLong(),
                resultDto.defender!!.id!!.toLong(),
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
    fun updateMatch(resultDto: MatchResultDto):Boolean{
        return crud.update(
                resultDto.attacker!!.username!!,
                resultDto.defender!!.username!!,
                resultDto.attacker!!.health!!,
                resultDto.defender!!.health!!,
                resultDto.attacker!!.damage!!,
                resultDto.defender!!.damage!!,
                resultDto.attacker!!.remainingHealth!!,
                resultDto.defender!!.remainingHealth!!,
                resultDto.winnerName!!,
                resultDto.id!!.toLong()
        )
    }
}
package no.ern.game.match.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
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

    // TODO: remove later, when finish with wiremock
    @GetMapping(path = arrayOf("/string"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun check() : ResponseEntity<String>{
        return ResponseEntity.ok("this is string from Match result")
    }

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
            return ResponseEntity.status(409).build()
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

    @ApiOperation("Update an existing matchResult")
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun update(
            @ApiParam(ID_PARAM)
            @PathVariable("id")
            pathId: String?,
            //
            @ApiParam("The matchResult that will replace the old one. Cannot change its id though.")
            @RequestBody
            dto: MatchResultDto
    ): ResponseEntity<Any> {
        val dtoId: Long

        try {
            dtoId = dto.id!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(404).build()
        }

        if (dto.id!! != pathId) {
            // In this case, 409 (Conflict) sounds more appropriate than the generic 400
            return ResponseEntity.status(409).build()
        }

        if (!crud.exists(dtoId)) {
            //Here, in this API, made the decision to not allow to create a news with PUT.
            // So, if we cannot find it, should return 404 instead of creating it
            return ResponseEntity.status(404).build()
        }

//        if (dto.text == null || dto.authorId == null || dto.country == null || dto.creationTime == null) {
//            return ResponseEntity.status(400).build()
//        }
        if(!updateMatch(dto))
            return ResponseEntity.status(400).build()


        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Modify the winnername of given matchResult ID")
    @PatchMapping(path = arrayOf("/{id}"),
            // could have had a custom type here, but then would need an unmarshaller for it
            consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateWinnerName(@ApiParam("The unique id of the MatchResult")
              @PathVariable("id")
              id: Long,
            //
              @ApiParam("""
                  The instructions on how to modify the winnername.
                  In this specific matchResult, it should be a single string value
              """)
              @RequestBody
              winnerName: String)
            : ResponseEntity<Void> {

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
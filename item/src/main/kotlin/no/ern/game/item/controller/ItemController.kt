package no.ern.game.item.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ern.game.item.domain.converters.ItemConverter
import no.ern.game.item.domain.dto.ItemDto
import no.ern.game.item.domain.enum.Type
import no.ern.game.item.repository.ItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.ConstraintViolationException

@Api(value = "/items", description = "API for items.")
@RequestMapping(
        path = arrayOf("/items"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@Validated
class ItemController {



    //TODO getAllItems (return a collection of all items)
    //TODO createItem (POST a new Item)
    //TODO getItems (minLevel, maxLevel, type)
    //TODO deleteItems
    //TODO updateItem
    //TODO replaceItem
    @Autowired
    private lateinit var repo: ItemRepository


    @ApiOperation("Get all items")
    @GetMapping
    fun getItems(@ApiParam("The type of the item")
                 @RequestParam("type", required = false)
                 type: Type?): ResponseEntity<Iterable<ItemDto>> {

        return if (type != null)
            ResponseEntity.ok(ItemConverter.transform(repo.getItemsByType(type)))
        else
            ResponseEntity.ok(ItemConverter.transform(repo.findAll()))

    }

    @ApiOperation("Create a new item")
    @PostMapping
    fun createNewItem(
            @ApiParam("Item to create")
            @RequestBody
            itemDto: ItemDto): ResponseEntity<Long> {

        try{
            val id = createItem(itemDto)
            return ResponseEntity.status(201).body(id)
        }catch (e: ConstraintViolationException){
            // 422 Unprocessable Entity
            // 409 Conflict (for duplication id)
            return ResponseEntity.status(422).build()
        }catch (e: Exception){
            return ResponseEntity.status(500).build()
        }

    }

    @ApiOperation("Create a new item")
    @DeleteMapping(path = arrayOf("/{id}"))
    fun deleteItemById(
            @ApiParam("The ID of the item")
            @PathVariable("id")
            pathId: String?): ResponseEntity<Any> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (!repo.exists(id)) {
            return ResponseEntity.status(404).build()
        }
        repo.delete(id)
        return ResponseEntity.status(204).build()
    }



    fun createItem(resultDto: ItemDto): Long{
            return repo.createItem(
                    resultDto.name!!,
                    resultDto.description!!,
                    resultDto.type!!,
                    resultDto.damageBonus!!,
                    resultDto.healthBonus!!,
                    resultDto.price!!,
                    resultDto.levelRequirement!!)
    }

}
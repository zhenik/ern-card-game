package no.ern.game.item.controller

import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import io.swagger.annotations.*
import no.ern.game.item.domain.converters.ItemConverter
import no.ern.game.schema.dto.ItemDto
import no.ern.game.item.domain.enum.Type
import no.ern.game.item.repository.ItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.TransactionRequiredException
import javax.validation.ConstraintViolationException

@Api(value = "/items", description = "API for items.")
@RequestMapping(
        path = arrayOf("/items"),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE)
)
@RestController
@DefaultProperties(defaultFallback = "fallback")
@Validated
class ItemController {

    @Autowired
    private lateinit var repo: ItemRepository


    @ApiOperation("Get all items or get items by type or get items by maxlevel&minlevel or get items by list of ids")
    @ApiResponse(code = 200, message = "List of items")
    @GetMapping
    @HystrixCommand
    fun getItems(@ApiParam("The type of the item, and/or maxlevel&minlevel")
                 @RequestParam
                 requestParams: Map<String, String>?,
                 @ApiParam("List of ids")
                 @RequestParam ("ids", required = false)
                 ids: List<Long>?
                    ): ResponseEntity<Iterable<ItemDto>> {



        val minLevel = requestParams?.get("minLevel")
        val maxLevel = requestParams?.get("maxLevel")
        val type = requestParams?.get("type")

        // GET ../items?type=passedType
        return if (type != null && maxLevel == null && minLevel == null) {
            if(!validEnum(type)) return ResponseEntity.status(400).build()
            val convertedType = Type.valueOf(type)
            ResponseEntity.ok(ItemConverter.transform(repo.getItemsByType(convertedType)))
        }
        // GET ../items?minLevel=passedMinLevel&maxLevel=passedMaxLevel
        // GET ../items?minLevel=passedMinLevel&maxLevel=paddedMaxLevel&type=passedType
        else if(minLevel != null && maxLevel!= null)
        {
            val convertedMinLevel= Integer.parseInt(minLevel)
            val convertedMaxLevel= Integer.parseInt(maxLevel)
            if(type != null){
                val convertedType = Type.valueOf(type)
                ResponseEntity.ok(ItemConverter.transform(repo.getItemsByLevelAndType(convertedMinLevel, convertedMaxLevel, convertedType)))
            }
            ResponseEntity.ok(ItemConverter.transform(repo.getItemsByLevel(convertedMinLevel, convertedMaxLevel)))
        }
        //GET ../items/?ids=id,id,id...
        else if(ids != null){
            return ResponseEntity.ok(ItemConverter.transform(repo.findAll(ids)))
        }
        // GET ../items
        else
            ResponseEntity.ok(ItemConverter.transform(repo.findAll()))

    }

    @ApiOperation("Get a single item specified by id")
    @ApiResponses(
            ApiResponse(code = 400, message = "Could not parse the id"),
            ApiResponse(code = 404, message = "No items on this id exists"),
            ApiResponse(code = 200, message = "Item with this id")
    )
    @GetMapping(path = arrayOf("/{id}"))
    fun getItem(@ApiParam("The ID of the item")
                       @PathVariable("id")
                       pathId: String?)
            : ResponseEntity<ItemDto> {

        val id: Long
        try {
            id = pathId!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        val itemDto = repo.findOne(id) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(ItemConverter.transform(itemDto))
    }

    @ApiOperation("Create a new item")
    @ApiResponses(
            ApiResponse(code = 201, message = "Id of the created item"),
            ApiResponse(code = 409, message = "Constraint Violation: Given DTO does not follow the constraints"),
            ApiResponse(code = 400, message = "Invalid DTO")
    )
    @PostMapping
    fun createNewItem(
            @ApiParam("Item to create")
            @RequestBody
            itemDto: ItemDto): ResponseEntity<Long> {

        try{
            val id = createItem(itemDto)
            return ResponseEntity.status(201).body(id)
        }catch (e: ConstraintViolationException){
            return ResponseEntity.status(409).build()
        }catch (e: Exception){
            return ResponseEntity.status(400).build()
        }

    }

    @ApiOperation("Remove an item")
    @ApiResponses(
            ApiResponse(code = 204, message = "Item has been deleted"),
            ApiResponse(code = 400, message = "Invalid ID, could not be parsed"),
            ApiResponse(code = 404, message = "This item does not exist")
    )
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

    @ApiOperation("Replace an existing item by ID")
    @ApiResponses(
            ApiResponse(code = 201, message = "Item did not exist of this item, but a new one has been created. Returned id of new item"),
            ApiResponse(code = 204, message = "Successfully replaced the item, but has nothing to return"),
            ApiResponse(code = 400, message = "Invalid ID in path or invalid DTO"),
            ApiResponse(code = 409, message = "The path ID and DTO ID do not match")
    )
    @PutMapping(path = arrayOf("/{id}"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun replace(
            @ApiParam("The ID of the item to replace")
            @PathVariable("id")
            pathId: String?,
            //
            @ApiParam("The item to replace the old one on the same ID")
            @RequestBody
            dto: ItemDto
    ): ResponseEntity<Any> {
        val dtoId: Long

        try {
            dtoId = dto.id!!.toLong()
        } catch (e: Exception) {
            return ResponseEntity.status(400).build()
        }

        if (dto.id!! != pathId) {
            return ResponseEntity.status(409).build()
        }

        if (!repo.exists(dtoId)) {
            createItem(dto)
            return ResponseEntity.status(201).build()
        }

        if(!replaceItem(dto))
            return ResponseEntity.status(400).build()


        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Change the name of the item")
    @ApiResponses(
            ApiResponse(code = 204, message = "Successfully updated the item name"),
            ApiResponse(code = 400, message = "Could not update the name"),
            ApiResponse(code = 404, message = "Item does not exist on this id")
    )
    @PatchMapping(path = arrayOf("/{id}"),
            // could have had a custom type here, but then would need an unmarshaller for it
            consumes = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    fun updateItemName(@ApiParam("The ID of the item to patch")
                         @PathVariable("id")
                         id: Long,
                         @ApiParam("The name to change to")
                         @RequestBody
                         name: String)
            : ResponseEntity<Void> {

        if (!repo.exists(id)) {
            return ResponseEntity.status(404).build()
        }

        if(!repo.updateItemName(id, name)){
            return ResponseEntity.status(400).build()
        } else {
            return ResponseEntity.status(204).build()
        }
    }

    @GetMapping(path = arrayOf("/fail"))
    @HystrixCommand
    fun failItem()
            : String {
        throw Exception()
        return "This will never be reached"
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

    fun replaceItem (resultDto: ItemDto):Boolean{
        return repo.replace(
                resultDto.name!!,
                resultDto.description!!,
                resultDto.type!!,
                resultDto.damageBonus!!,
                resultDto.healthBonus!!,
                resultDto.price!!,
                resultDto.levelRequirement!!,
                resultDto.id!!.toLong()
        )
    }

     fun fallback(): String{
        return "Something went wrong"
    }

    fun validEnum(enum: String): Boolean{

        val type: String = enum

        if (type == "Weapon" || type == "Armor" || type == "Undefined")
            return true
        return false
    }

}
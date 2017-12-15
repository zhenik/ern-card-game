package no.ern.game.schema.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel("DTO representing Player")
data class PlayerDto(

        @ApiModelProperty("Username of the player")
        var username: String? = null,

        @ApiModelProperty("The id of the player")
        var id: String? = null,

        @ApiModelProperty("Player's health")
        var health: Int? = 1,

        @ApiModelProperty("Player's damage")
        var damage: Int? = 1,

        @ApiModelProperty("Amount of money the player has")
        var currency: Int? = null,

        @ApiModelProperty("Player's experience")
        var experience: Int? = null,

        @ApiModelProperty("Player's current level")
        var level: Int? = 1,

        @ApiModelProperty("The items the player has")
        var items: Collection<Long>? = null
): Serializable


/**
 * example
    {
        "username": "name",
        "id": "1",
        "health": 100,
        "damage": 10,
        "currency": 100,
        "experience": 0,
        "level": 1,
        "items": []
    }
*/

package no.ern.game.player.domain.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel("DTO representing Player")
data class PlayerDto(

        @ApiModelProperty("The id of the player")
        var id: String? = null,

        @ApiModelProperty("Username of the player")
        var username: String? = null,

        @ApiModelProperty("Hashed password")
        var password: String? = null,

        @ApiModelProperty("Salt of the hashed password, if required by hashing algorithm")
        var salt: String? = null,

        @ApiModelProperty("Player's health")
        var health: Int? = 1,
        @ApiModelProperty("Player's damage")
        var damage: Int? = 1,

//        @ApiModelProperty("Image in a Binary format, if exists")
//        var avatar: Blob? = null,

        @ApiModelProperty("Amount of money the user has")
        var currency: Int? = null,

        @ApiModelProperty("Player's experience level")
        var experience: Int? = null,

        @ApiModelProperty("Player's current level")
        var level: Int? = 1,

        @ApiModelProperty("The items the player has")
        var equipment: Collection<Long>? = null
): Serializable


/**
 * example
    {
        "id":"1",
        "username":"SUPERGUY",
        "password":"pdasd",
        "salt":"super salt",
        "health":100,
        "damage":10,
        "currency":100,
        "experience":0,
        "level":3,
        "equipment":[]
    }
*/

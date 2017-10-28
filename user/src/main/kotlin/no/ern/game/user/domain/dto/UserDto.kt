package no.ern.game.user.domain.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.ern.game.user.domain.model.ItemEntity
import java.sql.Blob

@ApiModel("DTO representing User")
data class UserDto(

        @ApiModelProperty("The id of the User")
        var id: String? = null,

        @ApiModelProperty("Username of the user")
        var username: String? = null,

        @ApiModelProperty("Hashed password")
        var password: String? = null,

        @ApiModelProperty("Salt of the hashed password, if required by hashing algorithm")
        var salt: String? = null,

        @ApiModelProperty("User's health")
        var health: Int? = 1,
        @ApiModelProperty("User's damage")
        var damage: Int? = 1,

        @ApiModelProperty("Image in a Binary format, if exists")
        var avatar: Blob? = null,

        @ApiModelProperty("Amount of money the user has")
        var currency: Int? = null,

        @ApiModelProperty("User's experience level")
        var experience: Int? = null,

        @ApiModelProperty("User's current level")
        var level: Int? = 1,

        @ApiModelProperty("The items the user has")
        var equipment: Collection<ItemEntity>? = null
)


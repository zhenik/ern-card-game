package no.ern.game.schema.dto.gamelogic

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing player. It uses for searching opponent (find enemy)")
data class PlayerSearchDto(

        @ApiModelProperty("The id of the Player")
        var id: String? = null,

        @ApiModelProperty("Username of the Player")
        var username: String? = null,

        @ApiModelProperty("User's current level")
        var level: Int? = 1
)

/**
 *  example

    {
        "id":"1",
        "username":"dangerPlayer",
        "level":1
    }

 */


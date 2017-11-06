package no.ern.game.schema.dto.gamelogic

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing player. It uses for searching(hunting) opponent")
data class PlayerSearchDto(

        @ApiModelProperty("The id of the User")
        var id: String? = null,

        @ApiModelProperty("Username of the user")
        var username: String? = null,

        @ApiModelProperty("User's current level")
        var level: Int? = 1
)


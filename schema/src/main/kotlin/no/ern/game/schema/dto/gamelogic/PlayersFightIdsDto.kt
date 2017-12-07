package no.ern.game.schema.dto.gamelogic

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

//TODO: discuss by id or by username (both are unique)
@ApiModel("DTO representing players who are going fight with each other")
data class PlayersFightIdsDto(

        @ApiModelProperty("The id of the attacker")
        var attackerId: String? = null,

        @ApiModelProperty("The id of the defender")
        var defenderId: String? = null
)

/**
 *  example

    {
        "attackerId":"1",
        "defenderId":"2"
    }

 */
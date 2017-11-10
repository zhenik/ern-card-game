package no.ern.game.schema.dto.gamelogic

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * game
 * NIK on 10/11/2017
 */
@ApiModel("DTO representing fight log")
data class FightResultLogDto(

        @ApiModelProperty("The attacker username")
        var attackerUsername: String? = null,

        @ApiModelProperty("The defender username")
        var defenderUserName: String? = null,

        @ApiModelProperty("The winner of the match(fight)")
        var winner: String? = null,

        @ApiModelProperty("User's current level")
        var gameLog: MutableMap<String,String>? = null
)

/**
 *  example

    {
        "attackerUsername":"superGuy",
        "defenderUserName":"RobertBaratheon",
        "winner":"superGuy",
        "gameLog":  {
            "1":"superGuy MAKE  22 TO RobertBaratheon | NEW STATE: [superGuy HEALTH: 61/112] & [RobertBaratheon HEALTH: -7/125]",
            "2":"superGuy MAKE  22 TO RobertBaratheon | NEW STATE: [superGuy HEALTH: 61/112] & [RobertBaratheon HEALTH: -7/125]",
            "3":"RobertBaratheon MAKE  17 TO superGuy | NEW STATE: [RobertBaratheon HEALTH: -7/125] & [superGuy HEALTH: 61/112]"
            ....
        }
    }

 */
package no.ern.game.schema.dto.gamelogic

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty


@ApiModel("DTO representing fight log")
data class FightResultLogDto(

        @ApiModelProperty("The attacker username")
        var attackerUsername: String? = null,

        @ApiModelProperty("The defender username")
        var defenderUsername: String? = null,

        @ApiModelProperty("The winner of the match(fight)")
        var winner: String? = null,

        @ApiModelProperty("Fight log")
        var gameLog: MutableMap<Int,String>? = null
)

/**
 *  example

    {
        "attackerUsername": "attackerName",
        "defenderUsername": "defenderName",
        "winner": "attackerName",
        "gameLog": {
            "1": " defenderName --> attackerName [dmg] - [16] | defenderName [124/124] | attackerName [86/102]",
            "2": " attackerName --> defenderName [crit dmg ] - [24] | attackerName [86/102] | defenderName [100/124]",
            "3": " defenderName --> attackerName [dmg] - [16] | defenderName [100/124] | attackerName [70/102]",
            "4": " defenderName --> attackerName [dmg] - [16] | defenderName [100/124] | attackerName [54/102]",
            "5": " defenderName --> attackerName [crit dmg ] - [32] | defenderName [100/124] | attackerName [22/102]",
            "6": " attackerName --> defenderName [crit dmg ] - [24] | attackerName [22/102] | defenderName [76/124]",
            "7": " defenderName --> attackerName [dmg] - [16] | defenderName [76/124] | attackerName [6/102]",
            "8": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [64/124]",
            "9": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [52/124]",
            "10": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [40/124]",
            "11": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [28/124]",
            "12": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [16/124]",
            "13": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [4/124]",
            "14": " attackerName --> defenderName [dmg] - [12] | attackerName [6/102] | defenderName [-8/124]"
        }
    }

 */
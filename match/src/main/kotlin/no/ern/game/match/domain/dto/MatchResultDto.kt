package no.ern.game.match.domain.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("MatchResult representation. Data transfer object represents match result")
data class MatchResultDto(

        @ApiModelProperty("Username 1")
        var attackerUsername: String?=null,
        @ApiModelProperty("Username 2")
        var defenderUsername: String?=null,

        @ApiModelProperty("Total health of attacker")
        var attackerHealth: Long?=null,
        @ApiModelProperty("Total health of defender")
        var defenderHealth: Long?=null,


        @ApiModelProperty("Total damage that attacker made for defender")
        var attackerTotalDamage: Long?=null,
        @ApiModelProperty("Total damage that defender made for attacker")
        var defenderTotalDamage: Long?=null,

        @ApiModelProperty("Remaining health of attacker, when match is finished")
        var attackerRemainingHealth: Long?=null,
        @ApiModelProperty("Remaining health of defender, when match is finished")
        var defenderRemainingHealth: Long?=null,


        @ApiModelProperty("Winner name of the match")
        var winnerName: String?= null,

        @ApiModelProperty("MatchResult id")
        var id: String?=null
)

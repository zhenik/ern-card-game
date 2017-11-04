package no.ern.game.schema.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("MatchResult representation. Data transfer object represents match result")
data class MatchResultDto(

        @ApiModelProperty("Attacker info")
        var attacker: PlayerDto? = null,

        @ApiModelProperty("Defender info")
        var defender: PlayerDto? = null,

        @ApiModelProperty("Winner name of the match")
        var winnerName: String?= null,

        @ApiModelProperty("MatchResult id")
        var id: String?=null
)

data class PlayerDto(
        @ApiModelProperty("Username")
        var username: String?=null,
        @ApiModelProperty("Total health")
        var health: Long?=null,
        @ApiModelProperty("Total damage")
        var damage: Long?=null,
        @ApiModelProperty("Remaining health, when match is finished")
        var remainingHealth: Long?=null
)

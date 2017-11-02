package no.ern.game.match.domain.dto

import io.swagger.annotations.ApiModelProperty

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

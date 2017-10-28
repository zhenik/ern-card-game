package no.ern.game.match.domain.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("MatchResult representation. Data transfer object represents match result")
data class MatchResultDto(

        @ApiModelProperty("Username 1")
        var username1: String?=null,

        @ApiModelProperty("Username 2")
        var username2: String?=null,

        @ApiModelProperty("Total damage that user with username1 made for enemy")
        var totalDamage1: Long?=null,

        @ApiModelProperty("Total damage that user with username2 made for enemy")
        var totalDamage2: Long?=null,

        @ApiModelProperty("Remaining health of user with username1, when match is finished")
        var remainingHealth1: Long?=null,

        @ApiModelProperty("Remaining health of user with username2, when match is finished")
        var remainingHealth2: Long?=null,

        @ApiModelProperty("Winner name of the match")
        var winnerName: String?= null,

        @ApiModelProperty("MatchResult id")
        var id: String?=null
)

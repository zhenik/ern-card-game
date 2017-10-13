package no.ern.game.api.domain.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty


@ApiModel("Quiz representation for creating reading")
data class EntityDto(

        @ApiModelProperty("The id of the entity")
        var id: String?=null,

        @ApiModelProperty("Field 1")
        var field1: String?=null,

        @ApiModelProperty("Field 2")
        var field2: String?=null
)
package no.ern.game.item.domain.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for items")
data class ItemDto(

        @ApiModelProperty("The name of the item")
        var name: String?=null,

        @ApiModelProperty("Description of the item")
        var description: String?=null,

        @ApiModelProperty("What type the items is (WEAPON/ARMOR)")
        var type: String?=null,

        @ApiModelProperty("How much extra damage this item gives")
        var damageBonus: Long?=null,

        @ApiModelProperty("How much extra health this item gives")
        var healthBonus: Long?=null,

        @ApiModelProperty("Item id")
        var id: String?=null
)

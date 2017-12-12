package no.ern.game.item.domain.converters

import no.ern.game.schema.dto.ItemDto
import no.ern.game.item.domain.model.Item


class ItemConverter{

    companion object {

        fun transform(item: Item) : ItemDto{
            return ItemDto(
                    id=item.id?.toString(),
                    name = item.name,
                    description = item.description,
                    type = item.type.toString(),
                    damageBonus = item.damageBonus,
                    healthBonus = item.healthBonus,
                    price = item.price,
                    levelRequirement = item.levelRequirement
            )
        }

        fun transform(items: Iterable<Item>) : Iterable<ItemDto>{
            return items.map { transform(it) }
        }
    }
}
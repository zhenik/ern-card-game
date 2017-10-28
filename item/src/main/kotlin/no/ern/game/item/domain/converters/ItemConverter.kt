package no.ern.game.item.domain.converters

import no.ern.game.item.domain.dto.ItemDto
import no.ern.game.item.domain.model.Item

//TODO: Update converter to reflect DTO and Item

class ItemConverter{

    companion object {

        fun transform(item: Item) : ItemDto{
            return ItemDto(
                    id=item.id?.toString(),
                    name = item.name,
                    description = item.description,
                    type = item.type,
                    damageBonus = item.damageBonus,
                    healthBonus = item.healthBonus
            )
        }

        //TODO: return iterable
        fun transform(items: Iterable<Item>) : List<ItemDto>{
            return items.map { transform(it) }
        }
    }
}
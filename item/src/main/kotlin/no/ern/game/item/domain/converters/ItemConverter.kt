package no.ern.game.item.domain.converters

import no.ern.game.item.domain.dto.ItemDto
import no.ern.game.item.domain.model.Item

class ItemConverter{

    companion object {

        fun transform(item: Item) : ItemDto{
            return ItemDto(
                    id=item.id?.toString(),
                    name = item.name,
                    description = item.description
            )
        }

        fun transform(items: Iterable<Item>) : List<ItemDto>{
            return items.map { transform(it) }
        }
    }
}
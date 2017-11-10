package no.ern.game.item.domain.converters

import no.ern.game.item.domain.enum.Type
import no.ern.game.item.domain.model.Item
import no.ern.game.schema.dto.ItemDto
import org.junit.Assert.*
import org.junit.Test

class ItemConverterTest {

    @Test
    fun testTransform(){

        val item1 = Item(
                "Sword", "blabla", Type.Weapon, 10, 10, 100, 2
        )

        val item2 = Item(
                "Chest plate", "blabla", Type.Armor, 10, 10, 100, 2
        )

        val dto = ItemConverter.transform(item1)
        val list : List<ItemDto> = ItemConverter.transform(listOf(item1, item2)).toList()


        //Assert

        assertEquals(item1.type, Type.valueOf("Weapon"))
        assertNull(dto.id)
    }
}
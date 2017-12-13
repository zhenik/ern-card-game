package no.ern.game.gamelogic.domain.converters

import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.gamelogic.domain.model.Character

import org.junit.Assert.*
import org.junit.Test

class PlayerFightConverterTest{

    val items = listOf(
            ItemDto("sword","long sword","good",10,0, 100, 1, "1"),
            ItemDto("armor","metal heavy armor","excelent",0,5 ,100, 1,"3")
    )

    val playerDto = PlayerDto(
            "username1",
            "1",
            100,
            10,
            100,
            100,
            1,
            listOf(1,3)) // TODO: assume that items have these ids or validate it? (Because it will be delegated to item module)

    @Test
    fun testTransform() {

        // Act
        val character: Character = PlayerFightConverter.transform(playerDto,items)

        // Assert
        assertEquals(20, character.damage)
        assertEquals(105, character.health)
    }

}
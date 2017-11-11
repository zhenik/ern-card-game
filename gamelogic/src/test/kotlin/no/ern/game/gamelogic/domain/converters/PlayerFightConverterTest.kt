package no.ern.game.gamelogic.domain.converters

import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.UserDto
import no.ern.game.gamelogic.domain.converters.PlayerFightConverter
import no.ern.game.gamelogic.domain.model.Player

import org.junit.Assert.*
import org.junit.Test

class PlayerFightConverterTest{

    val items = listOf(
            ItemDto("sword","long sword","good",10,0),
            ItemDto("armor","metal heavy armor","excelent",0,5)
    )
    val userDto = UserDto(
            "1",
            "attackerName",
            "some pass",
            "some salt",
            100,
            10,
            305,
            1000,
            1,
            listOf(1,3))

    @Test
    fun testTransform(){

        // Act
        val player : Player = PlayerFightConverter.transform(userDto,items)

        // Assert
        assertEquals(20,player.damage)
        assertEquals(105,player.health)
    }

}
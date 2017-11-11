package no.ern.game.gamelogic.domain.converters

import no.ern.game.schema.dto.UserDto
import org.junit.Assert.*
import org.junit.Test


class PlayerSearchConverterTest{
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
        val playerSearchResult = PlayerSearchConverter.transform(userDto)

        // Assert
        assertEquals(userDto.id,playerSearchResult.id)
        assertEquals(userDto.username, playerSearchResult.username)
        assertEquals(userDto.level, playerSearchResult.level)
    }
}
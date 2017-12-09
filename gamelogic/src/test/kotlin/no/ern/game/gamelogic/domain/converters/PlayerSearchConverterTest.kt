package no.ern.game.gamelogic.domain.converters

import no.ern.game.schema.dto.PlayerDto
import org.junit.Assert.*
import org.junit.Test


class PlayerSearchConverterTest{
    val playerDto = PlayerDto(
            "1",
            "username1",
            "1",
            100,
            10,
            100,
            100,
            1,
            listOf(1,3))

    @Test
    fun testTransform(){

        // Act
        val playerSearchResult = PlayerSearchConverter.transform(playerDto)

        // Assert
        assertEquals(playerDto.id,playerSearchResult.id)
        assertEquals(playerDto.username, playerSearchResult.username)
        assertEquals(playerDto.level, playerSearchResult.level)
    }
}
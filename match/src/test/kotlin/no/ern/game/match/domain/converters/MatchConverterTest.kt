package no.ern.game.match.domain.converters

import no.ern.game.match.domain.model.Match
import org.junit.Assert.*
import org.junit.Test

/**
 * game
 * NIK on 27/10/2017
 */
class MatchConverterTest{
    @Test
    fun testTransform(){

        //Arrange
        val match1= Match("u1", "u2", 25, 5, 0, 5, "u2")
        val match2= Match("u1", "u2", 25, 5, 0, 5, "u2", 123L)

        //Act
        val entDto = MatchConverter.transform(match1)
        val list = MatchConverter.transform(listOf(match1,match2))

        //Assert
        assertEquals(match1.remainingHealth1,entDto.remainingHealth1)
        assertNull(entDto.id)

        //YoDa style
        assertTrue(list.stream().anyMatch { match1.remainingHealth1.equals(it.remainingHealth1)})
        assertTrue(list.stream().anyMatch { match2.username1.equals(it.username1)})
    }
}
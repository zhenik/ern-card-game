package no.ern.game.match.domain.converters

import no.ern.game.match.domain.model.MatchResult
import org.junit.Assert.*
import org.junit.Test

/**
 * game
 * NIK on 27/10/2017
 */
class MatchResultResultConverterTest {
    @Test
    fun testTransform(){

        //Arrange
        val match1= MatchResult("u1", "u2", 25, 5, 0, 5, "u2")
        val match2= MatchResult("u1", "u2", 25, 5, 0, 5, "u2", 123L)

        //Act
        val entDto = MatchResultConverter.transform(match1)
        val list = MatchResultConverter.transform(listOf(match1,match2))

        //Assert
        assertEquals(match1.remainingHealth1,entDto.remainingHealth1)
        assertNull(entDto.id)

        //YoDa style
        assertTrue(list.stream().anyMatch { match1.remainingHealth1.equals(it.remainingHealth1)})
        assertTrue(list.stream().anyMatch { match2.username1.equals(it.username1)})
    }
}
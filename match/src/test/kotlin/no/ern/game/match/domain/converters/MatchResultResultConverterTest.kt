package no.ern.game.match.domain.converters

import no.ern.game.match.domain.dto.MatchResultDto
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
        val match1= MatchResult(
                "u1",
                "u2",
                25,
                20,
                20,
                15,
                5,
                0,
                "u1")
        val match2= MatchResult(
                "u22",
                "u11",
                25,
                20,
                20,
                15,
                5,
                0,
                "u22")



        //Act
        val entDto = MatchResultConverter.transform(match1)
        val list : List<MatchResultDto> = MatchResultConverter.transform(listOf(match1,match2)).toList()

        //Assert
        assertEquals(match1.attackerRemainingHealth,entDto.attackerRemainingHealth)
        assertNull(entDto.id)

        //YoDa style
        assertTrue(list.stream().anyMatch { match1.attackerRemainingHealth==(it.attackerRemainingHealth)})
        assertTrue(list.stream().anyMatch { match2.attackerUsername==(it.attackerUsername)})
    }
}
package no.ern.game.api.domain.converters

import no.ern.game.api.domain.model.Entity
import org.junit.Assert.*
import org.junit.Test

/**
 * game
 * NIK on 13/10/2017
 */
class EntityConverterTest{

    @Test
    fun testTransform(){

        //Arrange
        val entity = Entity("C3", "PO")

        //Act
        val entDto = EntityConverter.transform(entity)

        //Assert
        assertEquals(entity.field1,entDto.field1)
        assertEquals(entity.field2,entDto.field2)
        assertNull(entDto.id) // that's tricky, check converter `?`
    }
}
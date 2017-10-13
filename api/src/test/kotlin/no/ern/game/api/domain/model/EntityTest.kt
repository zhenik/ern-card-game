package no.ern.game.api.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * game
 * NIK on 13/10/2017
 */
class EntityTest{

    @Test
    fun testEmptyConstructor(){
        //Arrange
        val entity = Entity()

        //Assert
        assertEquals("",entity.field1)
        assertEquals("",entity.field2)
        assertNull(entity.id)
    }

    @Test
    fun testFullName(){
        //Arrange
        val entity = Entity("C3","PO")

        //Act
        val fullName = entity.fullName()

        //Assert
        assertEquals("C3PO",fullName)
    }

}
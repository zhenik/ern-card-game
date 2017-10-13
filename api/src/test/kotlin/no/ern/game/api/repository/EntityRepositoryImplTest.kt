package no.ern.game.api.repository

import no.ern.game.api.domain.model.Entity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@RunWith(SpringRunner::class)
@DataJpaTest
class EntityRepositoryImplTest{

    @Autowired
    private lateinit var crud: EntityRepository

    @Test
    fun testCreateValid(){

        //Arrange
        val entity = Entity("C3","PO")

        //Act
        val entityFromDb = crud.save(entity)

        //Assert
        assertNotNull(entityFromDb.id)
    }



}
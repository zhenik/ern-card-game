package no.ern.game.user.repository

import junit.framework.Assert.assertEquals
import no.ern.game.user.domain.model.UserEntity
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class EntityRepositoryImplTest {

    @Autowired
    private lateinit var repo: UserRepository

    @Test
    fun testNoCrash() {
        assertEquals(true, true)
    }
}
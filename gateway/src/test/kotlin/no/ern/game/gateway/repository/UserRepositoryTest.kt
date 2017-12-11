package no.ern.game.gateway.repository

import no.ern.game.gateway.domain.model.User
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var repo: UserRepository

    @Before
    fun setup() {
        Assert.assertEquals(0, repo.count())
    }

    @Test
    fun createUser() {
        val user = User("name","password")

        repo.save(user)

        assertEquals(1, repo.count())
    }

    @Test
    fun createUser_Custom() {
        // Act
        val id = repo.createUser("name","password")

        // Assert
        assertTrue(id>0)
        assertEquals(1, repo.count())
    }

    @Test
    fun findUserByUserName(){
        // Arrange
        val id = repo.createUser("name","password")
        assertTrue(id>0)

        // Act
        val user = repo.findUserByUsername("name")
            // note: do not throw exception
        val user1 = repo.findUserByUsername("notExist")


        // Assert
        assertNull(user1)
        assertEquals("password", user!!.password)
    }
}

package no.ern.game.gateway.service

import no.ern.game.gateway.repository.UserRepository
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

//    @Autowired
//    private lateinit var repo: UserRepository

    @Before
    fun setup() {
//        Assert.assertEquals(0, repo.count())
    }

    @Test
    fun createUser(){
        // Act
        val created = userService.createUserWithHashedPassword("name", "password")

        // Assert
        assertTrue(created)
//        val user = repo.findUserByUsername("name")
//
//        assertNotEquals("password",user!!.password)
//        println(user!!.password)

    }
}
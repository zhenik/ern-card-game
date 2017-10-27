package no.ern.game.user.repository

import no.ern.game.user.domain.model.UserEntity
import org.junit.Assert.*
import org.junit.Before
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

    @Before
    fun setup() {
        assertEquals(0, repo.count())
    }

    @Test
    fun testNoCrash() {
        assertEquals(true, true)
    }

    @Test
    fun testCreateUser() {
        val user = getValidTestUser()
        val savedUser = repo.save(user)

        assertNotNull(savedUser)
        assertNotNull(savedUser.id)
    }

    @Test
    fun testCreatingDuplicateUsername() {
        val user1 = getValidTestUser()
        val user2 = getValidTestUser()

        var savedUser1: UserEntity? = null
        var savedUser2: UserEntity? = null

        try {
            savedUser1 = repo.save(user1)
            savedUser2 = repo.save(user2)
            fail()
        } catch (e: Exception) {
            assertNotNull(savedUser1)
            assertNull(savedUser2)

            assertNotNull(savedUser1?.id)
            assertNull(savedUser2?.id)
        }

    }

    private fun getValidTestUser(): UserEntity {
        return UserEntity(
                "Ruby",
                "ThisIsAHash",
                "ThisIsSomeSalt",
                120,
                44,
                null,
                40,
                1,
                1
        )
    }

    fun createUser(user: UserEntity): Long {
        return repo.createUser(
                username = user.username,
                password = user.password,
                salt = user.salt,
                health = user.health,
                damage = user.damage,
                avatar = user.avatar,
                currency = user.currency,
                experience = user.experience,
                level = user.level,
                equipment = user.equipment
        )
    }
}


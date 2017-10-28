package no.ern.game.user.repository

import no.ern.game.user.domain.model.User
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
        val user = getValidTestUsers()[0]
        val savedUser = repo.save(user)

        assertNotNull(savedUser)
        assertNotNull(savedUser.id)
    }

    @Test
    fun testCreatingDuplicateUsername() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[0]

        var savedUser1: User? = null
        var savedUser2: User? = null

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

    @Test
    fun testFindFirstByUsername() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[1]
        val savedUser1Id = createUser(user1)
        val savedUser2Id = createUser(user2)

        // Needs to be persisted, to avoid reading from cache.
        val savedUser1 = repo.findFirstByUsername(user1.username)
        val savedUser2 = repo.findFirstByUsername(user2.username)

        assertNotNull(savedUser1Id)
        assertNotNull(savedUser2Id)

        assertEquals(user1.username, savedUser1.username)
        assertEquals(user1.password, savedUser1.password)

        assertEquals(user2.username, savedUser2.username)
        assertEquals(user2.password, savedUser2.password)

        assertEquals(savedUser1Id, savedUser1.id)
        assertEquals(savedUser2Id, savedUser2.id)
    }

    @Test
    fun testRangeAnnotation() {

    }


    private fun getValidTestUsers(): List<User> {
        return listOf(
                User(
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
                        null,
                        40,
                        1,
                        1
                ),
                User(
                        "Kotlin",
                        "Spicy language..",
                        "Thisshouldalsobesalted",
                        122,
                        46,
                        null,
                        47,
                        1,
                        1
                )
        )
    }

    fun createUser(user: User): Long {
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


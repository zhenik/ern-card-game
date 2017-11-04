package no.ern.game.user.repository

import no.ern.game.user.domain.model.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import javax.validation.ConstraintViolationException

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
        val savedId = createUser(user)

        assertTrue(repo.exists(savedId))
        assertEquals(1, repo.count())
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

        assertEquals(user1.username, savedUser1?.username)
        assertEquals(user1.password, savedUser1?.password)

        assertEquals(user2.username, savedUser2?.username)
        assertEquals(user2.password, savedUser2?.password)

        assertEquals(savedUser1Id, savedUser1?.id)
        assertEquals(savedUser2Id, savedUser2?.id)
    }

    @Test
    fun testFindUserByLevel() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[0]
        user2.username = "asdasjdoasjdioasoidj"
        val user3 = getValidTestUsers()[1]

        assertEquals(0, repo.findAllByLevel(user1.level).count())

        createUser(user1)
        createUser(user2)
        createUser(user3)
        val usersFound1 = repo.findAllByLevel(user1.level)
        val usersFound2 = repo.findAllByLevel(user3.level)

        assertEquals(2, usersFound1.count())
        assertTrue(usersFound1.any({ e -> e.username == user1.username }))
        assertTrue(usersFound1.any({ e -> e.username == user2.username }))

        assertEquals(1, usersFound2.count())
        assertTrue(usersFound2.any({ e -> e.username == user3.username }))
    }

    @Test
    fun testUpdateUser() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[1]

        val savedId = createUser(user1)
        assertEquals(1, repo.count())

        val wasSuccessful = updateUser(user2, savedId)
        assertEquals(true, wasSuccessful)

        val readUser = repo.findFirstByUsername(user2.username)

        assertEquals(readUser?.username, user2.username)
        assertEquals(readUser?.id, savedId)
        assertEquals(readUser?.password, user2.password)

        assertEquals(1, repo.count())
    }

    @Test
    fun testUpdateUserIdChange() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[1]

        val savedId = createUser(user1)

        val wasSuccessful = updateUser(user2, savedId * 2)
        assertEquals(false, wasSuccessful)

        val userFound = repo.findFirstByUsername(user2.username)
        assertNull(userFound)
        assertEquals(1, repo.count())
    }

    @Test
    fun testUpdateUserWithTooLongUsername() {
        val user1 = getValidTestUsers()[0]
        val savedId = createUser(user1)

        user1.username = getTooLongUsername()
        val wasSuccessful = updateUser(user1, savedId)

        assertEquals(false, wasSuccessful)
        assertEquals(1, repo.count())
    }

    @Test
    fun testSetUsername() {
        val user1 = getValidTestUsers()[0]
        val newUsername = getValidTestUsers()[1].username

        val savedId = createUser(user1)
        assertEquals(1, repo.count())

        repo.setUsername(newUsername, savedId)

        val findUser = repo.findFirstByUsername(newUsername)

        assertEquals(newUsername, findUser?.username)
        assertEquals(user1.health, findUser?.health)
        assertEquals(user1.password, findUser?.password)
        assertEquals(1, repo.count())
    }

    @Test
    fun testSetUsernameTooLong() {
        val user1 = getValidTestUsers()[0]
        val tooLongUsername = getTooLongUsername()

        val savedId = createUser(user1)

        val wasSuccessful = repo.setUsername(tooLongUsername, savedId)
        assertEquals(false, wasSuccessful)

        assertNull(repo.findFirstByUsername(tooLongUsername))
        assertNotNull(repo.findFirstByUsername(user1.username))
        assertEquals(user1.username,repo.findOne(savedId).username)
    }

    @Test
    fun testSetUsernameWrongId() {
        val user1 = getValidTestUsers()[0]
        val newUsername = getValidTestUsers()[1].username
        val savedId = createUser(user1)

        val wasSuccessful = repo.setUsername(newUsername, savedId * 2)
        assertEquals(false, wasSuccessful)

        val findUser = repo.findFirstByUsername(newUsername)
        assertNull(findUser)

        val findUser2 = repo.findFirstByUsername(user1.username)
        assertEquals(user1.username, findUser2?.username)
    }

    @Test
    fun testDeleteUser() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[1]
        createUser(user1)
        createUser(user2)

        assertEquals(2, repo.count())
        repo.deleteByUsername(user2.username)

        assertEquals(1, repo.count())

        repo.deleteByUsername(user1.username)
        assertEquals(0, repo.count())
    }

    @Test
    fun testExistsByUsername() {
        val user1 = getValidTestUsers()[0]
        val user2 = getValidTestUsers()[1]
        assertEquals(false, repo.existsByUsername(user1.username))

        createUser(user1)
        assertEquals(true, repo.existsByUsername(user1.username))

        assertEquals(false, repo.existsByUsername(user2.username))
    }

    @Test
    fun testDeleteUserWithWrongUsername() {
        val user1 = getValidTestUsers()[0]
        createUser(user1)

        assertEquals(1, repo.count())
        repo.deleteByUsername(getTooLongUsername())

        assertEquals(1, repo.count())
    }

    @Test
    fun testDeleteWhenNoUserExists() {
        assertEquals(0, repo.count())
        repo.deleteByUsername(getTooLongUsername())
        assertEquals(0, repo.count())
    }


    // Constraints
    @Test
    fun testPositiveIntegerConstraint() {
        val user = getValidTestUsers()[0]

        user.level = 500

        assertThatSavingUserFails(user)
    }

    @Test
    fun testNegativeIntegerConstraint() {
        val user = getValidTestUsers()[0]

        user.damage = -23

        assertThatSavingUserFails(user)
    }

    @Test
    fun testTooLongUsernameConstraint() {
        val user = getValidTestUsers()[1]

        user.username = getTooLongUsername()
        assertThatSavingUserFails(user)
    }

    @Test
    fun testBlankConstraint() {
        val user = getValidTestUsers()[1]

        user.username = "    "
        assertThatSavingUserFails(user)
    }

    private fun getTooLongUsername() =
            "somethingLongerThan50Characters_aoisdjasiojdaoisjdoaisdjisdijasdoiasdjaosidjaoisjdoaisjdaoisjdoiajsdiojasidojaosijdaoisjdoaisjdoaijsdiojasdiojasdoijaisodjaoisjdaoisjdoiasjdoiajsdoiajsdiojadoijdgapi nasdfasdioufhasdifasidfuhasdifhasodfihasduifhaisuodfhasidfh aohguidsfhuidhgsdfiuhsdiuofhgsdoifughsdioufhiusdfiusdfhgsidfhgsidofhgsdf"

    private fun assertThatSavingUserFails(user: User) {
        try {
            repo.save(user)
            fail()
        } catch (e: ConstraintViolationException) {

        }
        assertEquals(null, user.id)
    }


    fun getValidTestUsers(): List<User> {
        return listOf(
                User(
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
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
                        47,
                        23,
                        4
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
                currency = user.currency,
                experience = user.experience,
                level = user.level,
                equipment = user.equipment
        )
    }

    fun updateUser(user: User, id: Long): Boolean {
        return repo.updateUser(
                username = user.username,
                password = user.password,
                health = user.health,
                damage = user.damage,
                currency = user.currency,
                experience = user.experience,
                level = user.level,
                equipment = user.equipment,
                id = id
        )
    }
}


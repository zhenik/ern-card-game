package no.ern.game.player.repository

import no.ern.game.player.domain.model.Player
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
    private lateinit var repo: PlayerRepository

    @Before
    fun setup() {
        assertEquals(0, repo.count())
    }

    @Test
    fun testNoCrash() {
        assertEquals(true, true)
    }

    @Test
    fun testCreatePlayer() {
        val player = getValidPlayers()[0]
        val savedId = createPlayer(player)

        assertTrue(repo.exists(savedId))
        assertEquals(1, repo.count())
    }

    @Test
    fun testCreatingDuplicateUsername() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[0]

        var savedPlayer1: Player? = null
        var savedPlayer2: Player? = null

        try {
            savedPlayer1 = repo.save(player1)
            savedPlayer2 = repo.save(player2)
            fail()
        } catch (e: Exception) {
            assertNotNull(savedPlayer1)
            assertNull(savedPlayer2)

            assertNotNull(savedPlayer1?.id)
            assertNull(savedPlayer2?.id)
        }

    }

    @Test
    fun testFindFirstByUsername() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]
        val savedPlayer1Id = createPlayer(player1)
        val savedPlayer2Id = createPlayer(player2)

        // Needs to be persisted, to avoid reading from cache.
        val savedPlayer1 = repo.findFirstByUsername(player1.username)
        val savedPlayer2 = repo.findFirstByUsername(player2.username)

        assertNotNull(savedPlayer1Id)
        assertNotNull(savedPlayer2Id)

        assertEquals(player1.username, savedPlayer1?.username)
        assertEquals(player1.password, savedPlayer1?.password)

        assertEquals(player2.username, savedPlayer2?.username)
        assertEquals(player2.password, savedPlayer2?.password)

        assertEquals(savedPlayer1Id, savedPlayer1?.id)
        assertEquals(savedPlayer2Id, savedPlayer2?.id)
    }

    @Test
    fun testFindPlayerByLevel() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[0]
        player2.username = "asdasjdoasjdioasoidj"
        val player3 = getValidPlayers()[1]

        assertEquals(0, repo.findAllByLevel(player1.level).count())

        createPlayer(player1)
        createPlayer(player2)
        createPlayer(player3)
        val playersFound1 = repo.findAllByLevel(player1.level)
        val playersFound2 = repo.findAllByLevel(player3.level)

        assertEquals(2, playersFound1.count())
        assertTrue(playersFound1.any({ e -> e.username == player1.username }))
        assertTrue(playersFound1.any({ e -> e.username == player2.username }))

        assertEquals(1, playersFound2.count())
        assertTrue(playersFound2.any({ e -> e.username == player3.username }))
    }

    @Test
    fun testUpdatePlayer() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]

        val savedId = createPlayer(player1)
        assertEquals(1, repo.count())

        val wasSuccessful = updatePlayer(player2, savedId)
        assertEquals(true, wasSuccessful)

        val readPlayer = repo.findFirstByUsername(player2.username)

        assertEquals(readPlayer?.username, player2.username)
        assertEquals(readPlayer?.id, savedId)
        assertEquals(readPlayer?.password, player2.password)

        assertEquals(1, repo.count())
    }

    @Test
    fun testUpdatePlayerIdChange() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]

        val savedId = createPlayer(player1)

        val wasSuccessful = updatePlayer(player2, savedId * 2)
        assertEquals(false, wasSuccessful)

        val playerFound = repo.findFirstByUsername(player2.username)
        assertNull(playerFound)
        assertEquals(1, repo.count())
    }

    @Test
    fun testUpdatePlayerWithTooLongUsername() {
        val player1 = getValidPlayers()[0]
        val savedId = createPlayer(player1)

        player1.username = getTooLongUsername()
        val wasSuccessful = updatePlayer(player1, savedId)

        assertEquals(false, wasSuccessful)
        assertEquals(1, repo.count())
    }

    @Test
    fun testDeletePlayer() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]
        createPlayer(player1)
        createPlayer(player2)

        assertEquals(2, repo.count())
        repo.deleteByUsername(player2.username)

        assertEquals(1, repo.count())

        repo.deleteByUsername(player1.username)
        assertEquals(0, repo.count())
    }

    @Test
    fun testExistsByUsername() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]
        assertEquals(false, repo.existsByUsername(player1.username))

        createPlayer(player1)
        assertEquals(true, repo.existsByUsername(player1.username))

        assertEquals(false, repo.existsByUsername(player2.username))
    }

    @Test
    fun testDeletePlayerWithWrongUsername() {
        val player1 = getValidPlayers()[0]
        createPlayer(player1)

        assertEquals(1, repo.count())
        repo.deleteByUsername(getTooLongUsername())

        assertEquals(1, repo.count())
    }

    @Test
    fun testDeleteWhenNoPlayerExists() {
        assertEquals(0, repo.count())
        repo.deleteByUsername(getTooLongUsername())
        assertEquals(0, repo.count())
    }


    // Constraints
    @Test
    fun testPositiveIntegerConstraint() {
        val player = getValidPlayers()[0]

        player.level = 500

        assertThatSavingPlayerFails(player)
    }

    @Test
    fun testNegativeIntegerConstraint() {
        val player = getValidPlayers()[0]

        player.damage = -23

        assertThatSavingPlayerFails(player)
    }

    @Test
    fun testTooLongUsernameConstraint() {
        val player = getValidPlayers()[1]

        player.username = getTooLongUsername()
        assertThatSavingPlayerFails(player)
    }

    @Test
    fun testBlankConstraint() {
        val player = getValidPlayers()[1]

        player.username = "    "
        assertThatSavingPlayerFails(player)
    }

    private fun getTooLongUsername() =
            "somethingLongerThan50Characters_aoisdjasiojdaoisjdoaisdjisdijasdoiasdjaosidjaoisjdoaisjdaoisjdoiajsdiojasidojaosijdaoisjdoaisjdoaijsdiojasdiojasdoijaisodjaoisjdaoisjdoiasjdoiajsdoiajsdiojadoijdgapi nasdfasdioufhasdifasidfuhasdifhasodfihasduifhaisuodfhasidfh aohguidsfhuidhgsdfiuhsdiuofhgsdoifughsdioufhiusdfiusdfhgsidfhgsidofhgsdf"

    private fun assertThatSavingPlayerFails(player: Player) {
        try {
            repo.save(player)
            fail()
        } catch (e: ConstraintViolationException) {

        }
        assertEquals(null, player.id)
    }


    fun getValidPlayers(): List<Player> {
        return listOf(
                Player(
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
                        40,
                        1,
                        1,
                        listOf(1L, 3L, 2L)
                ),
                Player(
                        "Kotlin",
                        "Spicy language..",
                        "Thisshouldalsobesalted",
                        122,
                        46,
                        47,
                        23,
                        4,
                        listOf(10L, 25L, 17L)
                )
        )
    }

    fun createPlayer(player: Player): Long {
        return repo.createPlayer(
                username = player.username,
                password = player.password,
                salt = player.salt,
                health = player.health,
                damage = player.damage,
                currency = player.currency,
                experience = player.experience,
                level = player.level,
                equipment = player.equipment
        )
    }

    fun updatePlayer(player: Player, id: Long): Boolean {
        return repo.updatePlayer(
                username = player.username,
                password = player.password,
                health = player.health,
                damage = player.damage,
                currency = player.currency,
                experience = player.experience,
                level = player.level,
                equipment = player.equipment,
                id = id
        )
    }
}


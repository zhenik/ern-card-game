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
        createPlayer(player)

        assertTrue(repo.exists(player.id))
        assertEquals(1, repo.count())

        val foundPlayer = repo.findOne(player.id)
        foundPlayer.currency = player.currency
        foundPlayer.health = player.health
        foundPlayer.experience = player.experience
    }

    @Test
    fun testFindPlayerByLevel() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]
        player2.level = player1.level
        val player3 = getValidPlayers()[2]

        assertEquals(0, repo.findAllByLevel(player1.level).count())

        createPlayer(player1)
        createPlayer(player2)
        createPlayer(player3)
        val playersFound1 = repo.findAllByLevel(player1.level)
        val playersFound2 = repo.findAllByLevel(player3.level)

        assertEquals(2, playersFound1.count())
        assertTrue(playersFound1.any({ e -> e.health == player1.health }))
        assertTrue(playersFound1.any({ e -> e.health == player2.health }))

        assertEquals(1, playersFound2.count())
        assertTrue(playersFound2.any({ e -> e.health == player3.health }))
    }

    @Test
    fun testUpdatePlayer() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]

        createPlayer(player1)
        assertEquals(1, repo.count())

        val wasSuccessful = updatePlayer(player2, player1.id)
        assertEquals(true, wasSuccessful)

        val readPlayer = repo.findOne(player1.id)

        assertEquals(readPlayer?.health, player2.health)
        assertEquals(readPlayer?.id, player1.id)
        assertEquals(readPlayer?.currency, player2.currency)

        assertEquals(1, repo.count())
    }

    @Test
    fun testChangeIdByUpdate() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]

        createPlayer(player1)

        updatePlayer(player2, player1.id)

        val playerFound = repo.findOne(player2.id)
        assertNull(playerFound)
        assertEquals(1, repo.count())
    }

    @Test
    fun testDeletePlayer() {
        val player1 = getValidPlayers()[0]
        val player2 = getValidPlayers()[1]
        createPlayer(player1)
        createPlayer(player2)

        assertEquals(2, repo.count())
        repo.delete(player1.id)

        assertEquals(1, repo.count())

        repo.delete(player2.id)
        assertEquals(0, repo.count())
    }


    @Test
    fun testDeleteWhenNoPlayerExists() {
        assertEquals(0, repo.count())
        try {
            repo.delete(2323)
            fail("Delete id that doesnt should throw exception")
        } catch (e: Exception) {

        }
        assertEquals(0, repo.count())
    }


    // Constraints
    @Test
    fun testPositiveIntegerConstraint() {
        val player = getValidPlayers()[0]

        player.level = 9000

        createPlayer(player)

        val foundPlayer = repo.findOne(player.id)
        assertEquals(null, foundPlayer)
    }

    @Test
    fun testNegativeIntegerConstraint() {
        val player = getValidPlayers()[0]

        player.level = -23

        createPlayer(player)
        //assertEquals(false, isSaveSuccessful)

        val foundPlayer = repo.findOne(player.id)
        assertEquals(null, foundPlayer)
    }



    fun getValidPlayers(): List<Player> {
        return listOf(
                Player(
                        120,
                        44,
                        40,
                        1,
                        1,
                        listOf(1L, 3L, 2L),
                        1
                ),
                Player(
                        122,
                        46,
                        47,
                        23,
                        4,
                        listOf(10L, 25L, 17L),
                        5
                ),
                Player(
                        240,
                        96,
                        22,
                        222,
                        9,
                        listOf(11L, 27L, 19L),
                        13
                )
        )
    }

    fun createPlayer(player: Player): Boolean {
        return repo.createPlayer(
                health = player.health,
                damage = player.damage,
                currency = player.currency,
                experience = player.experience,
                level = player.level,
                equipment = player.equipment,
                id = player.id
        )
    }

    fun updatePlayer(player: Player, id: Long): Boolean {
        return repo.updatePlayer(
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


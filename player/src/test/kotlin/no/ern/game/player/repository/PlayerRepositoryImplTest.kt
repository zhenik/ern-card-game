package no.ern.game.player.repository

import no.ern.game.player.domain.model.Player
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
    private lateinit var repo: PlayerRepository

    @Before
    fun setup() {
        assertEquals(0, repo.count())
    }

    @Test
    fun testCreatePlayer_Valid() {
        val player = getValidPlayers()[0]

        val savedId = repo.createPlayer(
                player.userId,
                player.username,
                player.health,
                player.damage,
                player.currency,
                player.experience,
                player.level,
                player.items
        )

        assertEquals(1, repo.count())

        val foundPlayer = repo.findOne(savedId)
        foundPlayer.experience = player.experience
    }


    @Test
    fun testCreatePlayer_Invalid() {
        val player = getValidPlayers()[0]

        player.level = -1

        try {
            repo.createPlayer(
                    player.userId,
                    player.username,
                    player.health,
                    player.damage,
                    player.currency,
                    player.experience,
                    player.level,
                    player.items
            )
            fail()
        } catch (e: Exception) {

        }
    }

    @Test
    fun testAddItem_Valid() {
        val player = getValidPlayers()[0]
        val savedId = createPlayer(player)
        val expectedItemCount = player.items.count() + 1;

        assertEquals(repo.count(), 1)

        val wasSuccessful = repo.addItem(savedId, 300L)
        assertTrue(wasSuccessful)

        val foundPlayer = repo.findOne(savedId)
        assertEquals(expectedItemCount, foundPlayer.items.count())
    }

    @Test
    fun testAddItem_Invalid() {
        val player = getValidPlayers()[0]
        player.items = mutableSetOf(1L, 2L)

        val savedId = createPlayer(player)
        assertNotEquals(-1, savedId)

        val wasSuccessful = repo.addItem(savedId, 1L)
        assertFalse(wasSuccessful)
    }
//
//    @Test
//    fun testFindPlayerByLevel() {
//        val player1 = getValidPlayers()[0]
//        val player2 = getValidPlayers()[1]
//        // Change level so that we find more than one.
//        player2.level = player1.level
//        val player3 = getValidPlayers()[2]
//
//        createPlayer(player1)
//        createPlayer(player2)
//        createPlayer(player3)
//
//        assertEquals(3, repo.count())
//        val playersFound1 = repo.findAllByLevel(player1.level)
//        val playersFound2 = repo.findAllByLevel(player3.level)
//
//        assertEquals(2, playersFound1.count())
//        assertTrue(playersFound1.any({ e -> e.health == player1.health }))
//        assertTrue(playersFound1.any({ e -> e.health == player2.health }))
//
//        assertEquals(1, playersFound2.count())
//        assertTrue(playersFound2.any({ e -> e.health == player3.health }))
//    }
//
//    @Test
//    fun testUpdatePlayer() {
//        val player1 = getValidPlayers()[0]
//        val player2 = getValidPlayers()[1]
//
//        createPlayer(player1)
//        assertEquals(1, repo.count())
//
//        val wasSuccessful = updatePlayer(player2, player1.id!!)
//        assertEquals(true, wasSuccessful)
//
//        val readPlayer = repo.findOne(player1.id)
//
//        assertEquals(readPlayer?.health, player2.health)
//        assertEquals(readPlayer?.id, player1.id)
//        assertEquals(readPlayer?.currency, player2.currency)
//
//        assertEquals(1, repo.count())
//    }
//
//    @Test
//    fun testChangeIdByUpdate() {
//        val player1 = getValidPlayers()[0]
//        val player2 = getValidPlayers()[1]
//
//        createPlayer(player1)
//
//        updatePlayer(player2, player1.id!!)
//
//        val playerFound = repo.findOne(player2.id)
//        assertNull(playerFound)
//        assertEquals(1, repo.count())
//    }
//
//    @Test
//    fun testDeletePlayer() {
//        val player1 = getValidPlayers()[0]
//        val player2 = getValidPlayers()[1]
//        createPlayer(player1)
//        createPlayer(player2)
//
//        assertEquals(2, repo.count())
//        repo.delete(player1.id)
//
//        assertEquals(1, repo.count())
//
//        repo.delete(player2.id)
//        assertEquals(0, repo.count())
//    }
//
//
//    @Test
//    fun testDeleteWhenNoPlayerExists() {
//        assertEquals(0, repo.count())
//        try {
//            repo.delete(2323)
//            fail("Delete id that doesnt should throw exception")
//        } catch (e: Exception) {
//
//        }
//        assertEquals(0, repo.count())
//    }
//
//
//    // Constraints
//    @Test
//    fun testPositiveIntegerConstraint() {
//        val player = getValidPlayers()[0]
//
//        player.level = 9000
//
//        val wasSuccesful = createPlayer(player)
//        assertFalse(wasSuccesful)
//
//        val foundPlayer = repo.findOne(player.id)
//        assertNull(foundPlayer)
//    }
//
//    @Test
//    fun testNegativeIntegerConstraint() {
//        val player = getValidPlayers()[0]
//
//        player.level = -23
//
//        createPlayer(player)
//        //assertEquals(false, isSaveSuccessful)
//
//        val foundPlayer = repo.findOne(player.id)
//        assertEquals(null, foundPlayer)
//    }


    fun getValidPlayers(): List<Player> {
        return listOf(
                Player(
                        1,
                        "Bob",
                        120,
                        44,
                        40,
                        1,
                        1,
                        mutableSetOf(1L, 3L, 2L)
                ),
                Player(
                        5,
                        "Robert",
                        122,
                        46,
                        47,
                        23,
                        4,
                        mutableSetOf(10L, 25L, 17L)
                ),
                Player(
                        13,
                        "someone",
                        240,
                        96,
                        22,
                        222,
                        9,
                        mutableSetOf(11L, 27L, 19L)
                )
        )
    }


    fun createPlayer(player: Player): Long {
        val savedId = repo.createPlayer(
                player.userId,
                player.username,
                player.health,
                player.damage,
                player.currency,
                player.experience,
                player.level,
                player.items
        )

        return savedId
    }

    /*fun createPlayer(player: Player): Boolean {
        return repo.createPlayer(
                userId = player.userId,
                username = player.username,
                health = player.health,
                damage = player.damage,
                currency = player.currency,
                experience = player.experience,
                level = player.level,
                items = player.items
        )
    }*/

    fun updatePlayer(player: Player, id: Long): Boolean {
        return repo.updatePlayer(
                username = player.username,
                health = player.health,
                damage = player.damage,
                currency = player.currency,
                experience = player.experience,
                level = player.level,
                items = player.items,
                id = id
        )
    }
}


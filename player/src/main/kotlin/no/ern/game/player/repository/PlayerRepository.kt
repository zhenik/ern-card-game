package no.ern.game.player.repository

import no.ern.game.player.domain.model.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Repository
interface PlayerRepository : CrudRepository<Player, Long>, PlayerRepositoryCustom {
    fun findAllByLevel(level: Int): Iterable<Player>

    fun findAllByUsername(username: String): Iterable<Player>

    fun existsByUsername(username: String): Boolean
}

@Transactional
interface PlayerRepositoryCustom {
    fun createPlayer(
            userId: Long,
            username: String,
            health: Int,
            damage: Int,
            currency: Int,
            experience: Int,
            level: Int,
            items: MutableSet<Long>
    ): Long

    fun updatePlayer(
            username: String,
            health: Int,
            damage: Int,
            currency: Int,
            experience: Int,
            level: Int,
            items: MutableSet<Long>,
            id: Long): Boolean

    fun setCurrency(currency: Int, id: Long): Boolean

    fun addItem(id: Long, itemId: Long): Boolean
}

open class PlayerRepositoryImpl : PlayerRepositoryCustom {


    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createPlayer(userId: Long, username: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, items: MutableSet<Long>): Long {
        var id: Long = -1
        val playerEntity = Player(
                userId,
                username,
                health,
                damage,
                currency,
                experience,
                level,
                items
        )


        em.persist(playerEntity)

        if (playerEntity.id != null) {
            id = playerEntity.id!!
        }

        return id
    }

    override fun updatePlayer(username: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, items: MutableSet<Long>, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false

        if (username.isNullOrEmpty() || username.length > 50) {
            return false
        }

        if (currency < 0 ||
                damage < 1 ||
                level > 100 ||
                level < 1
                ) {
            return false
        }

        player.username = username
        player.health = health
        player.damage = damage
        player.currency = currency
        player.experience = experience
        player.level = level
        player.items = items

        return true
    }

    override fun setCurrency(currency: Int, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false
        if (currency < 0) {
            return false
        }
        player.currency = currency

        return true
    }

    override fun addItem(id: Long, itemId: Long): Boolean {
        var added = false
        try {
            val player = em.find(Player::class.java, id)
            added = player.items.add(itemId)
        } catch (e: Exception) {

        }
        return added
    }
}
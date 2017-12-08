package no.ern.game.player.repository

import no.ern.game.player.domain.model.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Repository
interface PlayerRepository : CrudRepository<Player, Long>, PlayerRepositoryCustom {
    fun findAllByLevel(level: Int): Iterable<Player>
}

@Transactional
interface PlayerRepositoryCustom {
    fun createPlayer(
            username: String,
            health: Int,
            damage: Int,
            currency: Int,
            experience: Int,
            level: Int,
            items: MutableCollection<Long>,
            id: Long): Boolean

    fun updatePlayer(username: String,
                     health: Int,
                     damage: Int,
                     currency: Int,
                     experience: Int,
                     level: Int,
                     items: MutableCollection<Long>,
                     id: Long): Boolean

    fun setCurrency(currency: Int, id: Long): Boolean
}

open class PlayerRepositoryImpl : PlayerRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createPlayer(username: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, items: MutableCollection<Long>, id: Long): Boolean {
        val playerEntity = Player(
                username,
                health,
                damage,
                currency,
                experience,
                level,
                items,
                id
        )
        try {
            em.persist(playerEntity)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun updatePlayer(username: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, items: MutableCollection<Long>, id: Long): Boolean {
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
}
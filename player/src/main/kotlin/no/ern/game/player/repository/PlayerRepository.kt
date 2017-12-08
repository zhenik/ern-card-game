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
            health: Int,
            damage: Int,
            currency: Int,
            experience: Int,
            level: Int,
            equipment: Collection<Long>,
            id: Long): Boolean

    fun updatePlayer(health: Int,
                     damage: Int,
                     currency: Int,
                     experience: Int,
                     level: Int,
                     equipment: Collection<Long>,
                     id: Long): Boolean

    fun setCurrency(currency: Int, id: Long): Boolean
}

open class PlayerRepositoryImpl : PlayerRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createPlayer(health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Long>, id: Long): Boolean {
        val playerEntity = Player(
                health,
                damage,
                currency,
                experience,
                level,
                equipment,
                id
        )
        try {
            em.persist(playerEntity)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun updatePlayer(health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Long>, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false

        if (currency < 0 ||
                damage < 1 ||
                level > 100 ||
                level < 1
                ) {
            return false
        }

        player.health = health
        player.damage = damage
        player.currency = currency
        player.experience = experience
        player.level = level
        player.equipment = equipment

        try {
            em.flush()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun setCurrency(currency: Int, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false
        if (currency < 0) {
            return false
        }

        player.currency = currency

        try {
            em.flush()
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
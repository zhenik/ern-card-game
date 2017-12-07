
package no.ern.game.player.repository

import no.ern.game.player.domain.model.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Repository
interface PlayerRepository : CrudRepository<Player, Long>, PlayerRepositoryCustom {
    fun findFirstByUsername(username: String): Player?
    fun findAllByLevel(level: Int): Iterable<Player>
    fun existsByUsername(username: String): Boolean

    @Transactional
    fun deleteByUsername(username: String): Long

}

@Transactional
interface PlayerRepositoryCustom {
    fun createPlayer(username: String,
                   password: String,
                   salt: String,
                   health: Int,
                   damage: Int,
                   currency: Int,
                   experience: Int,
                   level: Int,
                   equipment: Collection<Long>): Long

    fun updatePlayer(username: String,
                   password: String,
                   health: Int,
                   damage: Int,
                   currency: Int,
                   experience: Int,
                   level: Int,
                   equipment: Collection<Long>,
                   id: Long): Boolean

    fun setUsername(username: String, id: Long): Boolean
}

open class PlayerRepositoryImpl : PlayerRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createPlayer(username: String, password: String, salt: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Long>): Long {
        val playerEntity = Player(
                username,
                password,
                salt,
                health,
                damage,
                currency,
                experience,
                level,
                equipment
        )
        em.persist(playerEntity)
        return playerEntity.id!!
    }

    override fun updatePlayer(username: String, password: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Long>, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false

        if (username.isBlank() ||
                username.length > 50 ||
                currency < 0 ||
                damage < 1 ||
                level > 100 ||
                level < 1
                ) {
            return false
        }

        // Cannot take a username that already exists
        if (getPlayersByUsername(username).isNotEmpty()) {
            return false
        }

        player.username = username
        player.password = password
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

    fun getPlayersByUsername(username: String): List<Player> {
        val query = em.createQuery("SELECT u FROM Player u WHERE u.username = ?1", Player::class.java)
        query.setParameter(1, username)
        return query.resultList.toList()
    }

    override fun setUsername(username: String, id: Long): Boolean {
        val player = em.find(Player::class.java, id) ?: return false
        if (username.isNullOrBlank() || username.length > 50) {
            return false
        }

        player.username = username

        try {
            em.flush()
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
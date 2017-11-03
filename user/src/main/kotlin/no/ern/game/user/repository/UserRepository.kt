@file:Suppress("RedundantModalityModifier")

package no.ern.game.user.repository

import no.ern.game.user.domain.model.Item
import no.ern.game.user.domain.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface UserRepository : CrudRepository<User, Long>, UserRepositoryCustom {
    fun findFirstByUsername(username: String): User
    fun findAllByLevel(level: Int): Iterable<User>
    fun existsByUsername(username: String): Boolean

    @Transactional
    fun deleteByUsername(username: String): Long
}

@Transactional
interface UserRepositoryCustom {
    fun createUser(username: String,
                   password: String,
                   salt: String,
                   health: Int,
                   damage: Int,
                   currency: Int,
                   experience: Int,
                   level: Int,
                   equipment: Collection<Item>): Long

    fun updateUser(username: String,
                   password: String,
                   health: Int,
                   damage: Int,
                   currency: Int,
                   experience: Int,
                   level: Int,
                   equipment: Collection<Item>,
                   id: Long): Boolean
}

open class UserRepositoryImpl : UserRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createUser(username: String, password: String, salt: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Item>): Long {
        val userEntity = User(
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
        em.persist(userEntity)
        return userEntity.id!!
    }

    override fun updateUser(username: String, password: String, health: Int, damage: Int, currency: Int, experience: Int, level: Int, equipment: Collection<Item>, id: Long): Boolean {
        val user = em.find(User::class.java, id) ?: return false

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
        if (getUsersByUsername(username).isNotEmpty()) {
            return false
        }

        user.username = username
        user.password = password
        user.health = health
        user.damage = damage
        user.currency = currency
        user.experience = experience
        user.level = level
        user.equipment = equipment

        try {
            em.persist(user)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun getUsersByUsername(username: String): List<User> {
        val query = em.createQuery("SELECT u FROM User u WHERE u.username = ?1", User::class.java)
        query.setParameter(1, username)
        return query.resultList.toList()
    }
}
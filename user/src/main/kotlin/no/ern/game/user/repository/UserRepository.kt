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
interface UserRepository : CrudRepository<User,Long>, UserRepositoryCustom {
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
}

open class UserRepositoryImpl : UserRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createUser(username: String,
                            password: String,
                            salt: String,
                            health: Int,
                            damage: Int,
                            currency: Int,
                            experience: Int,
                            level: Int,
                            equipment: Collection<Item>
    ): Long {

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

}
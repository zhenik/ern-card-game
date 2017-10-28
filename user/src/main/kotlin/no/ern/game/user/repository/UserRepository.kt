package no.ern.game.user.repository

import no.ern.game.user.domain.model.ItemEntity
import no.ern.game.user.domain.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Blob
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface UserRepository : CrudRepository<User,Long>, UserRepositoryCustom {
    fun findFirstByUsername(username: String): User

}

@Transactional
interface UserRepositoryCustom {
    fun createUser(username: String,
                   password: String,
                   salt: String,
                   health: Int,
                   damage: Int,
                   avatar: Blob?,
                   currency: Int,
                   experience: Int,
                   level: Int,
                   equipment: Collection<ItemEntity>): Long
}

open class UserRepositoryImpl : UserRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createUser(username: String,
                            password: String,
                            salt: String,
                            health: Int,
                            damage: Int,
                            avatar: Blob?,
                            currency: Int,
                            experience: Int,
                            level: Int,
                            equipment: Collection<ItemEntity>
    ): Long {

        val userEntity = User(
                username,
                password,
                salt,
                health,
                damage,
                avatar,
                currency,
                experience,
                level,
                equipment
        )
        em.persist(userEntity)
        return userEntity.id!!
    }

}
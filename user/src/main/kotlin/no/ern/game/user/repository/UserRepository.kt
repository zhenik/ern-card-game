package no.ern.game.user.repository

import no.ern.game.user.domain.model.ItemMock
import no.ern.game.user.domain.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Blob
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface UserRepository : CrudRepository<UserEntity,Long>, UserRepositoryCustom

@Transactional
interface UserRepositoryCustom {
    fun saveUser(username: String,
                 password: String,
                 salt: String,
                 health: Int,
                 damage: Int,
                 avatar: Blob,
                 currency: Int,
                 experience: Int,
                 level: Int,
                 equipment: Collection<ItemMock>): Long
}

open class UserRepositoryImpl : UserRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun saveUser(username: String,
                          password: String,
                          salt: String,
                          health: Int,
                          damage: Int,
                          avatar: Blob,
                          currency: Int,
                          experience: Int,
                          level: Int,
                          equipment: Collection<ItemMock>): Long {

        val userEntity = UserEntity(
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
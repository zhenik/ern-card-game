package no.ern.game.gateway.repository

import no.ern.game.gateway.domain.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface UserRepository : CrudRepository<UserEntity, Long>, UserRepositoryCustom {
    fun findUserByUsername(username: String): UserEntity?
}

@Transactional
interface UserRepositoryCustom {

    fun createUser(
            username: String,
            password: String
    ): Long
}

open class UserRepositoryImpl : UserRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createUser(username: String, password: String): Long {
        var id : Long = 0
        val user = UserEntity(username,password)

        em.persist(user)

        if (user.id != null) {
            id = user.id!!
        }
        return id
    }
}



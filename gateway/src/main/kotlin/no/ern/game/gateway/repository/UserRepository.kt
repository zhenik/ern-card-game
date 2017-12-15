package no.ern.game.gateway.repository

import no.ern.game.gateway.domain.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, String> {
    fun findUserByUsername(username: String): UserEntity?
}
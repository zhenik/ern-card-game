package no.ern.game.api.repository

import no.ern.game.api.domain.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findUserByUsername(username: String): User
}
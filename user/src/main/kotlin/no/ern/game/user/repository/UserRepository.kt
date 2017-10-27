package no.ern.game.user.repository

import no.ern.game.user.domain.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository: CrudRepository<UserEntity, Long>, UserRepositoryCustom

@Transactional
interface UserRepositoryCustom {

}

open class UserRepositoryImpl : UserRepositoryCustom
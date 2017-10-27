package no.ern.game.user.repository

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository {

}

@Transactional
interface UserRepositoryCustom {

}

open class UserRepositoryImpl : UserRepositoryCustom
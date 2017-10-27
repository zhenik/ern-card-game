package no.ern.game.user.repository

import no.ern.game.user.domain.model.UserEntity
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class EntityRepositoryImplTest {

}

private fun getValidTestEntity(): UserEntity {
    return UserEntity(
            "Ruby",
            "ThisIsAHash",
            "ThisIsSomeSalt",
            120,
            44,
            null,
            40,
            1,
            1
    )
}
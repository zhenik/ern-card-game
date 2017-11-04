package no.ern.game.user.domain.converters

import no.ern.game.schema.dto.UserDto
import no.ern.game.user.domain.model.User

class UserConverter {

    companion object {

        fun transform(entity: User): UserDto {
            return UserDto(
                    id = entity.id.toString(),
                    username = entity.username,
                    password = entity.password,
                    salt = entity.salt,
                    health = entity.health,
                    damage = entity.damage,
                    currency = entity.currency,
                    experience = entity.experience,
                    level = entity.level,
                    equipment = entity.equipment
            )
        }

        fun transform(entities: Iterable<User>): Iterable<UserDto> {
            return entities.map { transform(it) }
        }
    }
}
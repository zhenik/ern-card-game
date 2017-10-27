package no.ern.game.user.domain.converters

import no.ern.game.user.domain.dto.UserDto
import no.ern.game.user.domain.model.UserEntity

class UserConverter {

    companion object {

        fun transform(entity: UserEntity): UserDto {
            return UserDto(
                    id = entity.id.toString(),
                    username = entity.username,
                    password = entity.password,
                    salt = entity.salt,
                    health = entity.health,
                    damage = entity.damage,
                    avatar = entity.avatar,
                    currency = entity.currency,
                    experience = entity.experience,
                    level = entity.level,
                    equipment = entity.equipment
            )
        }

        fun transform(entities: Iterable<UserEntity>): List<UserDto> {
            return entities.map { transform(it) }
        }
    }
}
package no.ern.game.gateway.domain.model

class UserConverter {
    companion object {

        fun transform(entity: UserEntity): UserDto {
            return UserDto(
                    username = entity.username,
//                    id = entity.id,
                    password = entity.password,
                    roles = entity.roles,
                    enabled = entity.enabled
            )
        }

        fun transform(entities: Iterable<UserEntity>): Iterable<UserDto> {
            return entities.map { transform(it) }
        }
    }
}

package no.ern.game.gateway.domain.model

class UserConverter {
    companion object {

        fun transform(entity: User): UserDto {
            return UserDto(
                    username = entity.username,
                    id = entity.id,
                    password = entity.password,
                    roles = entity.roles,
                    enabled = entity.enabled
            )
        }

        fun transform(entities: Iterable<User>): Iterable<UserDto> {
            return entities.map { transform(it) }
        }
    }
}

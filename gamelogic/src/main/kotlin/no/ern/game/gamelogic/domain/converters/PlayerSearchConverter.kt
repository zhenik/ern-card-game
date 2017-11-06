package no.ern.game.gamelogic.domain.converters

import no.ern.game.schema.dto.UserDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto

class PlayerSearchConverter {

    companion object {
        fun transform(entity: UserDto): PlayerSearchDto {
            return PlayerSearchDto(
                    id = entity.id.toString(),
                    username = entity.username,
                    level = entity.level
            )
        }

        fun transform(entities: Iterable<UserDto>): Iterable<PlayerSearchDto> {
            return entities.map { transform(it) }
        }
    }
}

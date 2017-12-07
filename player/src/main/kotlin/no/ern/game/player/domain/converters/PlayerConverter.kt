package no.ern.game.player.domain.converters

import no.ern.game.player.domain.model.Player
import no.ern.game.schema.dto.PlayerDto


class PlayerConverter {

    companion object {

        fun transform(entity: Player): PlayerDto {
            return PlayerDto(
                    id = entity.id.toString(),
                    health = entity.health,
                    damage = entity.damage,
                    currency = entity.currency,
                    experience = entity.experience,
                    level = entity.level,
                    equipment = entity.equipment
            )
        }

        fun transform(entities: Iterable<Player>): Iterable<PlayerDto> {
            return entities.map { transform(it) }
        }
    }
}
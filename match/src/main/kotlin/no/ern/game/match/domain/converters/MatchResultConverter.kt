package no.ern.game.match.domain.converters

import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerDto
import no.ern.game.match.domain.model.MatchResult

class MatchResultConverter {
    companion object {
        fun transform(entity: MatchResult) : MatchResultDto {
            return MatchResultDto(
                    id = entity.id?.toString(),
                    attacker = PlayerDto(
                            entity.attackerUsername,
                            entity.attackerHealth,
                            entity.attackerTotalDamage,
                            entity.attackerRemainingHealth),
                    defender = PlayerDto(
                            entity.defenderUsername,
                            entity.defenderHealth,
                            entity.defenderTotalDamage,
                            entity.defenderRemainingHealth),
                    winnerName = entity.winnerName
            )
        }
        fun transform(entities: Iterable<MatchResult>) : Iterable<MatchResultDto>{
            return entities.map { transform(it) }
        }
    }
}

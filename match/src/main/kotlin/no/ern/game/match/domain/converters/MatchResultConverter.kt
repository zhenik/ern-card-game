package no.ern.game.match.domain.converters

import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerResultDto
import no.ern.game.match.domain.model.MatchResult

class MatchResultConverter {
    companion object {
        fun transform(entity: MatchResult) : MatchResultDto {
            return MatchResultDto(
                    id = entity.id?.toString(),
                    attacker = PlayerResultDto(
                            entity.attackerId.toString(),
                            entity.attackerUsername,
                            entity.attackerHealth,
                            entity.attackerTotalDamage,
                            entity.attackerRemainingHealth),
                    defender = PlayerResultDto(
                            entity.defenderId.toString(),
                            entity.defenderUsername,
                            entity.defenderHealth,
                            entity.defenderTotalDamage,
                            entity.defenderRemainingHealth),
                    winnerName = entity.winnerName,
                    creationTime = entity.creationTime
            )
        }
        fun transform(entities: Iterable<MatchResult>) : Iterable<MatchResultDto>{
            return entities.map { transform(it) }
        }
    }
}

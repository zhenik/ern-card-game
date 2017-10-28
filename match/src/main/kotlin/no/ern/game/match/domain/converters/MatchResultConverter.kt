package no.ern.game.match.domain.converters

import no.ern.game.match.domain.dto.MatchResultDto
import no.ern.game.match.domain.model.MatchResult

class MatchResultConverter {
    companion object {
        fun transform(entity: MatchResult) : MatchResultDto {
            return MatchResultDto(
                    id=entity.id?.toString(),

                    attackerUsername = entity.attackerUsername,
                    defenderUsername = entity.defenderUsername,

                    attackerTotalDamage = entity.attackerTotalDamage,
                    defenderTotalDamage = entity.defenderTotalDamage,

                    attackerHealth = entity.attackerHealth,
                    defenderHealth = entity.defenderHealth,

                    attackerRemainingHealth = entity.attackerRemainingHealth,
                    defenderRemainingHealth = entity.defenderRemainingHealth,

                    winnerName = entity.winnerName
            )
        }
        fun transform(entities: Iterable<MatchResult>) : Iterable<MatchResultDto>{
            return entities.map { transform(it) }
        }
    }
}

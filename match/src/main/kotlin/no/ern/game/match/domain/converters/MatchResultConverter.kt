package no.ern.game.match.domain.converters

import no.ern.game.match.domain.dto.MatchResultDto
import no.ern.game.match.domain.model.MatchResult

class MatchResultConverter {
    companion object {
        fun transform(entity: MatchResult) : MatchResultDto {
            return MatchResultDto(
                    id=entity.id?.toString(),
                    username1 = entity.username1,
                    username2 = entity.username2,
                    totalDamage1 = entity.totalDamage1,
                    totalDamage2 = entity.totalDamage2,
                    remainingHealth1 = entity.remainingHealth1,
                    remainingHealth2 = entity.remainingHealth2,
                    winnerName = entity.winnerName
            )
        }
        fun transform(entities: Iterable<MatchResult>) : List<MatchResultDto>{
            return entities.map { transform(it) }
        }
    }
}

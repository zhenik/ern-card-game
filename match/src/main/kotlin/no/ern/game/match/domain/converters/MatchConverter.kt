package no.ern.game.match.domain.converters

import no.ern.game.match.domain.dto.MatchDto
import no.ern.game.match.domain.model.Match

class MatchConverter{
    companion object {
        fun transform(entity: Match) : MatchDto {
            return MatchDto(
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
        fun transform(entities: Iterable<Match>) : List<MatchDto>{
            return entities.map { transform(it) }
        }
    }
}

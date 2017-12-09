package no.ern.game.gamelogic.domain.converters

import no.ern.game.gamelogic.domain.model.Character
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.PlayerDto

class PlayerFightConverter {
    companion object {
        fun transform(player: PlayerDto, items: List<ItemDto>): Character {
            val healthBonus = items.sumBy { it.healthBonus!!.toInt() }
            val damageBonus = items.sumBy { it.damageBonus!!.toInt() }
            return Character(
                    username = player.username.toString(),
                    health = player.health!!.plus(healthBonus),
                    damage = player.damage!!.plus(damageBonus),
                    remainingHealth = player.health!!.plus(healthBonus)
            )
        }
    }
}
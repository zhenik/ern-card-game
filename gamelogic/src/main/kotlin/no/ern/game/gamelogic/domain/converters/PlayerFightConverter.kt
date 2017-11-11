package no.ern.game.gamelogic.domain.converters

import no.ern.game.gamelogic.domain.model.Player
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.UserDto

class PlayerFightConverter{
    companion object {
        fun transform(user: UserDto, items: List<ItemDto>): Player {
            val healthBonus = items.sumBy { it.healthBonus!!.toInt() }
            val damageBonus = items.sumBy { it.damageBonus!!.toInt() }
            return Player(
                    username = user.username.toString(),
                    health = user.health!!.plus(healthBonus),
                    damage = user.damage!!.plus(damageBonus),
                    remainingHealth = user.health!!.plus(healthBonus)
            )
        }
    }
}
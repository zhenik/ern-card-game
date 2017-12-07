package no.ern.game.gamelogic.domain.converters

import no.ern.game.gamelogic.domain.model.Character
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.UserDto

class PlayerFightConverter{
    companion object {
        fun transform(user: UserDto, items: List<ItemDto>): Character {
            val healthBonus = items.sumBy { it.healthBonus!!.toInt() }
            val damageBonus = items.sumBy { it.damageBonus!!.toInt() }
            return Character(
                    username = user.username.toString(),
                    health = user.health!!.plus(healthBonus),
                    damage = user.damage!!.plus(damageBonus),
                    remainingHealth = user.health!!.plus(healthBonus)
            )
        }
    }
}
package no.ern.game.gamelogic.services

import no.ern.game.schema.dto.PlayerDto
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperienceService{

    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    private lateinit var fanout: FanoutExchange

    fun levelUpPlayer(player: PlayerDto) {
        println("Sending " + player.toString())
        rabbitTemplate.convertAndSend(fanout.name, "", player)
    }
}
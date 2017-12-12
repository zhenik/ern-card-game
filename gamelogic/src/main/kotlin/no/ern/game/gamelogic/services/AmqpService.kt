package no.ern.game.gamelogic.services

import no.ern.game.schema.dto.MatchResultDto
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AmqpService{

    @Autowired
    private lateinit var rabbitTemplate : RabbitTemplate

    @Autowired
    private lateinit var fanout : FanoutExchange

    fun sendMatchResultCreated(matchResult: MatchResultDto){
        rabbitTemplate.convertAndSend(fanout.name,"",matchResult)
    }

}
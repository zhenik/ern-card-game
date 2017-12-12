package no.ern.game.match.service

import no.ern.game.schema.dto.MatchResultDto
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * game
 * NIK on 12/12/2017
 */
//@Service
//class RabbitListener{
//
//    @RabbitListener(queues = arrayOf("#{queue.name}"))
//    fun createMatchResult(matchResultDto: MatchResultDto){
//
//    }
//}
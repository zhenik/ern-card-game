package no.ern.game.gamelogic.controller

import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@Validated
class GameLogicController2 {



    @GetMapping(path = arrayOf("/username1"))
    fun currentUserName(authentication: Authentication): String {
        println(authentication)
        return authentication.toString()
    }


}
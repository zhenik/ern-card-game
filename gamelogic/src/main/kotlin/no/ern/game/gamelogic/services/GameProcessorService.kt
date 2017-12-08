package no.ern.game.gamelogic.services

import no.ern.game.gamelogic.domain.model.LogLine
import no.ern.game.gamelogic.domain.model.Character
import no.ern.game.schema.dto.gamelogic.FightResultLogDto
import org.springframework.stereotype.Service

@Service
class GameProcessorService {

    fun fight(attacker: Character, defender: Character):FightResultLogDto{

        val gameLog : MutableMap<Int,String> = HashMap()
        var counter = 1

        // game loop
        while (!isMatchFinished(attacker,defender)){
            // cube dice
            val aTry1 = dice()
            val aTry2 = dice()
            val dTry1 = dice()
            val dTry2 = dice()

            val aSum = aTry1.plus(aTry2)
            val dSum = dTry1.plus(dTry2)


            // attacker won dice
            if(aSum>dSum){
                if (aTry1==aTry2){
                    // critical dmg
                    defender.remainingHealth=defender.remainingHealth-(attacker.damage*2)
                    val logLine = LogLine(attacker,defender, true)
                    gameLog.put(counter++,logLine.toString())

                }
                else{
                    defender.remainingHealth=defender.remainingHealth-attacker.damage
                    val logLine = LogLine(attacker,defender, false)
                    gameLog.put(counter++,logLine.toString())
                }
            }

            // defender won dice
            if(aSum<dSum){
                if(dTry1==dTry2){
                    // critical
                    attacker.remainingHealth= attacker.remainingHealth-(defender.damage*2)
                    val logLine = LogLine(defender,attacker,true)
                    gameLog.put(counter++,logLine.toString())
                }
                else {
                    attacker.remainingHealth= attacker.remainingHealth-defender.damage
                    val logLine = LogLine(defender,attacker,false)
                    gameLog.put(counter++,logLine.toString())
                }
            }

            // if draw
            if(aSum==dSum){
                // do nothing
            }
        }


        val winner = if(attacker.remainingHealth<1) defender.username else attacker.username
        return FightResultLogDto(attacker.username,defender.username,winner,gameLog)

    }

    private fun isMatchFinished(attacker: Character, defender: Character):Boolean{
        return (attacker.remainingHealth<1 || defender.remainingHealth<1)
    }

    private fun dice():Int{
        return (Math.random()*6).toInt()+1
    }
}





//fun main(args: Array<String>) {
////    2.1 Users
//    val attackerUserDtoMock = PlayerDto("1","attackerName",null,null,100,10,null,null,1,null)
//    val defenderUserDtoMock = PlayerDto("2","defenderName",null,null,120,12,null,null,2,null)
//    //2.2 Items
//    val randomItems1: List<ItemDto> = listOf(
//            ItemDto(null,null,null,(Math.random()*15).toLong(),0),
//            ItemDto(null,null,null,0,(Math.random()*15).toLong())
//    )
//    val randomItems2: List<ItemDto> = listOf(
//            ItemDto(null,null,null,(Math.random()*15).toLong(),0),
//            ItemDto(null,null,null,0,(Math.random()*15).toLong())
//    )
//
//    val attacker: Character = PlayerFightConverter.transform(attackerUserDtoMock,randomItems1)
//    val defender: Character = PlayerFightConverter.transform(defenderUserDtoMock,randomItems2)

//    val service = GameProcessorService()
//    service.fight(attacker,defender)
//}
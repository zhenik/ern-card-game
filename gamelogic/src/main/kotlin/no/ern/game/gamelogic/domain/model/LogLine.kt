//package no.ern.game.gamelogic.domain.model
//
//
//class LogLine(
//        private var diceWinner: Character,
//        private var diceLoser: Character,
//        private var critical: Boolean
//){
//
//    override fun toString():String{
//        val dmgType = if(critical) "crit dmg " else "dmg"
//        val dmg = if(critical) (diceWinner.damage*2).toString() else diceWinner.damage.toString()
//
//        return " ${diceWinner.username} --> ${diceLoser.username} [$dmgType] - [$dmg] " +
//                "| ${diceWinner.username} [${diceWinner.remainingHealth}/${diceWinner.health}] " +
//                "| ${diceLoser.username} [${diceLoser.remainingHealth}/${diceLoser.health}]"
//    }
//}
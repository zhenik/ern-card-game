package no.ern.game.match.domain.model

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * game
 * NIK on 04/11/2017
 */
class MatchResultConstraintsValidator : ConstraintValidator<MatchResultConstraint, MatchResult> {

    override fun initialize(constraintAnnotation: MatchResultConstraint) {}

    override fun isValid(value: MatchResult, context: ConstraintValidatorContext): Boolean {
//         fighters should have unique names (cant fight with urself)
//         if(value.attackerUsername==value.defenderUsername) return false
//         xor one of players is winner, if both -> false, if none ->  false
        if ((value.winnerName == value.attackerUsername).xor(value.winnerName == value.defenderUsername))
            return true

        return false
    }
}
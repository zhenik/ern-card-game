package no.ern.game.match.domain.model

import java.lang.annotation.Documented
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = arrayOf(MatchResultConstraintsValidator::class))
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.ANNOTATION_CLASS)
@Retention(value = AnnotationRetention.RUNTIME)
@Documented
annotation class MatchResultConstraint(
        val message: String = "Invalid constraints in MatchResult state. ",
        val groups: Array<KClass<*>> = arrayOf(),
        val payload: Array<KClass<out Payload>> = arrayOf())
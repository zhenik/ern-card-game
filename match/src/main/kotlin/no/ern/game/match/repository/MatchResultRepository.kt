package no.ern.game.match.repository

import no.ern.game.match.domain.model.MatchResult
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Repository
interface MatchResultRepository : CrudRepository<MatchResult, Long>, MatchResultRepositoryCustom {
    fun findAllByAttackerUsernameOrDefenderUsername(attackerUsername: String, defenderUsername: String): Iterable<MatchResult>
    fun findAllByWinnerName(winnerName: String): Iterable<MatchResult>
}

@Transactional
interface MatchResultRepositoryCustom {
    fun createMatchResult(
            attackerUsername: String,
            defenderUsername: String,
            attackerHealth: Long,
            defenderHealth: Long,
            attackerTotalDamage: Long,
            defenderTotalDamage: Long,
            attackerRemainingHealth: Long,
            defenderRemainingHealth: Long,
            winnerName: String
    ): Long

    fun getMatchesByUserName(username: String): Iterable<MatchResult>
}

open class MatchResultRepositoryImpl : MatchResultRepositoryCustom {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createMatchResult(
            attackerUsername: String,
            defenderUsername: String,
            attackerHealth: Long,
            defenderHealth: Long,
            attackerTotalDamage: Long,
            defenderTotalDamage: Long,
            attackerRemainingHealth: Long,
            defenderRemainingHealth: Long,
            winnerName: String): Long {

        var id = -1L
        val match = MatchResult(
                attackerUsername,
                defenderUsername,
                attackerHealth,
                defenderHealth,
                attackerTotalDamage,
                defenderTotalDamage,
                attackerRemainingHealth,
                defenderRemainingHealth,
                winnerName
        )
        em.persist(match)

        if (match.id!=null) id = match.id!!

        return id
    }

    override fun getMatchesByUserName(username: String): Iterable<MatchResult> {
        val query = em.createQuery("select m from MatchResult m where m.attackerUsername = ?1 OR m.defenderUsername=?2", MatchResult::class.java)
        query.setParameter(1, username)
        query.setParameter(2, username)
        return query.resultList.toList()
    }
}
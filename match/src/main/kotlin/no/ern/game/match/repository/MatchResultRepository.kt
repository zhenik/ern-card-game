package no.ern.game.match.repository

import no.ern.game.match.domain.model.MatchResult
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
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
            attackerId: Long,
            defenderId: Long,
            attackerUsername: String,
            defenderUsername: String,
            attackerHealth: Int,
            defenderHealth: Int,
            attackerTotalDamage: Int,
            defenderTotalDamage: Int,
            attackerRemainingHealth: Int,
            defenderRemainingHealth: Int,
            winnerName: String
    ): Long

    fun update(
            attackerUsername: String,
            defenderUsername: String,
            attackerHealth: Int,
            defenderHealth: Int,
            attackerTotalDamage: Int,
            defenderTotalDamage: Int,
            attackerRemainingHealth: Int,
            defenderRemainingHealth: Int,
            winnerName: String,
            id: Long
    ): Boolean

    fun changeWinnerName(id: Long, newWinnerName: String):Boolean

    fun getMatchesByUserName(username: String): Iterable<MatchResult>

    fun getLastMatchResultByUserName(username: String): MatchResult?
}

open class MatchResultRepositoryImpl : MatchResultRepositoryCustom {

    override fun changeWinnerName(id: Long, newWinnerName: String): Boolean {
        val matchResult = em.find(MatchResult::class.java, id) ?: return false
        if (newWinnerName.isNullOrBlank())return false
        matchResult.winnerName=newWinnerName
        return true

    }

    override fun update(attackerUsername: String, defenderUsername: String, attackerHealth: Int, defenderHealth: Int, attackerTotalDamage: Int, defenderTotalDamage: Int, attackerRemainingHealth: Int, defenderRemainingHealth: Int, winnerName: String, id: Long): Boolean {

        val matchResult = em.find(MatchResult::class.java, id) ?: return false
        if (winnerName != attackerUsername && winnerName != defenderUsername) return false

        if (
            attackerUsername.isNullOrBlank() ||
            defenderUsername.isNullOrBlank() ||
            attackerHealth<0 ||
            defenderHealth<0 ||
            attackerTotalDamage<0 ||
            defenderTotalDamage<0) return false

        matchResult.attackerUsername = attackerUsername
        matchResult.defenderUsername = defenderUsername
        matchResult.attackerHealth = attackerHealth
        matchResult.defenderHealth = defenderHealth
        matchResult.attackerTotalDamage = attackerTotalDamage
        matchResult.defenderTotalDamage = defenderTotalDamage
        matchResult.attackerRemainingHealth = attackerRemainingHealth
        matchResult.defenderRemainingHealth = defenderRemainingHealth
        matchResult.winnerName = winnerName
        return true
    }

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createMatchResult(
            attackerId: Long,
            defenderId: Long,
            attackerUsername: String,
            defenderUsername: String,
            attackerHealth: Int,
            defenderHealth: Int,
            attackerTotalDamage: Int,
            defenderTotalDamage: Int,
            attackerRemainingHealth: Int,
            defenderRemainingHealth: Int,
            winnerName: String): Long {


        var id = -1L

//        if (winnerName != attackerUsername && winnerName != defenderUsername) return id

        val match = MatchResult(
                attackerId,
                defenderId,
                attackerUsername,
                defenderUsername,
                attackerHealth,
                defenderHealth,
                attackerTotalDamage,
                defenderTotalDamage,
                attackerRemainingHealth,
                defenderRemainingHealth,
                winnerName,
                ZonedDateTime.now()
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

    override fun getLastMatchResultByUserName(username: String): MatchResult? {
        val query = em.createQuery("select m from MatchResult m where m.attackerUsername = ?1 OR m.defenderUsername=?2 ORDER BY m.creationTime DESC", MatchResult::class.java)
        query.setParameter(1, username)
        query.setParameter(2, username)
        return query.setMaxResults(1).singleResult
    }
}
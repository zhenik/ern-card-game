package no.ern.game.match.repository

import no.ern.game.match.domain.model.MatchResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import javax.validation.ConstraintViolationException

@RunWith(SpringRunner::class)
@DataJpaTest
class MatchResultRepositoryTestImpl {

    @Autowired
    private lateinit var crud: MatchResultRepository

    @Before
    fun setup(){
        assertEquals(0, crud.count())
    }

    private fun createMatchDefault() : Long {
        return crud.createMatch(
                "u4321",
                "u123",
                25,
                30,
                0,
                5,
                "u123")
    }
    private fun createMatchForUsername(username: String) : Long {
        return crud.createMatch(
                username,
                "u2",
                25,
                30,
                0,
                5,
                "u2")
    }
    private fun createMatch(match: MatchResult): MatchResult? {
        return crud.save(match)
    }


    @Test
    fun testCreateValid() {

        //Act
        val id = crud.createMatch(
                "u1",
                "u2",
                25,
                30,
                0,
                5,
                "u2")
        //Assert
        assertNotNull(id)
        assertTrue(!id.equals(-1L))
    }

    @Test
    fun testCreate3Valid() {

        //Act
        val id1 = createMatchDefault();
        val id2 = createMatchDefault();
        val id3 = createMatchDefault();

        //Assert
        assertNotNull(id1)
        assertNotNull(id2)
        assertNotNull(id3)
        assertEquals(3, crud.count())
    }

    @Test
    fun testCreateNotValid_TotalDamageNegative() {


        //Act
        try{
            crud.createMatch(
                    "u1",
                    "u2",
                    25,
                    -4,
                    0,
                    5,
                    "u2")
            fail()
        }
        catch (e : ConstraintViolationException){}

        try{
            crud.createMatch(
                    "u1",
                    "u2",
                    25,
                    0,
                    0,
                    -5,
                    "u2")
            fail()
        }
        catch (e : ConstraintViolationException){}
    }

    @Test
    fun testCreateNotValid_RemainingHealthNegative() {

        //Act
        try{
            crud.createMatch(
                    "u1",
                    "u2",
                    25,
                    -4,
                    0,
                    5,
                    "u2")
            fail()
        }
        catch (e : ConstraintViolationException){}
    }

    @Test
    fun testGetMatchesForUsername(){

        //Arrange
        val username = "Alesha"
        createMatchForUsername(username)
        createMatchForUsername(username)
        createMatchForUsername(username)
        createMatchForUsername("someRandomGuy")

        //Act
        // auto-generated
        val list1 : List<MatchResult> = crud.findAllByUsername1OrUsername2(username, username) as List<MatchResult>
        // custom
        val list2 : List<MatchResult> = crud.getMatchesByUserName(username) as List<MatchResult>

        //Assert
        assertEquals(3, list1.size)
        assertEquals(3, list2.size)
        assertEquals(4, crud.count())
        assertTrue(list1.containsAll(list2))
    }

    @Test
    fun testAllByWinnerName(){

        //Arrange
        val winnerName = "u2"
        val match1= MatchResult("u1", winnerName, 25, 5, 0, 5, winnerName)
        val match2= MatchResult("u1", winnerName, 25, 5, 0, 5, winnerName)

        crud.save(match1)
        crud.save(match2)
        createMatchDefault()
        createMatchDefault()

        //Act
        val list: List<MatchResult> = crud.findAllByWinnerName(winnerName) as List<MatchResult>

        //Assert
        assertEquals(4, crud.count())
        assertEquals(2, list.size)
        assertTrue(list.stream().allMatch { it.winnerName.equals(winnerName) })

    }


}
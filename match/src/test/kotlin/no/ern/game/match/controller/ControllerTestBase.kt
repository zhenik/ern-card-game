package no.ern.game.match.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ern.game.match.Application
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerResultDto
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.logging.Level
import java.util.logging.Logger

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class),
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ControllerTestBase{

    private val logger : Logger = Logger.getLogger(ControllerTestBase::class.java.canonicalName)

    @LocalServerPort
    protected var port = 0

    /**
     * if we do not provide application.yml for tests,
     * Spring automatically load it from source root
     * */
//    @Value("\${server.contextPath}")
//    private lateinit var contextPath : String

    @Before
    @After
    fun clean() {

        logger.log(Level.INFO, port.toString())
//        logger.log(Level.INFO, contextPath)



        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/matches"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        val list = RestAssured.given().accept(ContentType.JSON).get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<MatchResultDto>::class.java)
                .toList()



        list.stream().forEach {
            RestAssured.given().pathParam("id", it.id)
                    .delete("/{id}")
                    .then()
                    .statusCode(204)
        }

        RestAssured.given().get()
                .then()
                .statusCode(200)
                .body("size()", CoreMatchers.equalTo(0))
    }

    fun getValidMatchResultDto(): MatchResultDto {
        val attackerWinner = PlayerResultDto("1","u1", 30, 28, 5)
        val defender  = PlayerResultDto("2","u2", 25, 25, -3)
        return MatchResultDto(attackerWinner, defender, attackerWinner.username)
    }

    fun postNewMatchResultValid(dto: MatchResultDto) : Long{
        return RestAssured.given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)
    }
}
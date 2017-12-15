package no.ern.game.e2etests

import org.testcontainers.containers.DockerComposeContainer
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit


class GameLogicDockerIT {
        companion object {

        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)


        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)

        private var counter = System.currentTimeMillis()

        @BeforeClass
        @JvmStatic
        fun initialize() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 10000
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()


            await().atMost(240, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until({
                        // check GATEWAY is available
                        given().get("http://localhost:10000/api/v1/user").then().statusCode(401)

                        true
                    })
        }
    }


    @Test
    fun testUnauthorizedAccess() {
        RestAssured.given().get("/api/v1/gamelogic-server/play/enemy")
                .then()
                .statusCode(401)

        RestAssured.given().get("/api/v1/gamelogic-server/play/fight")
                .then()
                .statusCode(401)
    }

    @Test
    fun findEnemyAndFight() {
        // Arrange
        val id1 = createUniqueId()
        val cookie1 = registerUser(id1, "password")

        //try to find when there are only u in player db (its exclude urself from enemy list and return not found)
        await().atMost(60, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until({
                    RestAssured.given()
                        .cookie("SESSION", cookie1.session)
                        .get("/api/v1/gamelogic-server/play/enemy")
                        .then()
                        .statusCode(404)
                    true
                })

        // create a new user
        val id2 = createUniqueId()
        val cookie2 = registerUser(id2, "password")

        // find enemy return enemy and 200OK
        val responseFindEnemy = RestAssured.given()
                .cookie("SESSION", cookie2.session)
                .accept(ContentType.JSON)
                .get("/api/v1/gamelogic-server/play/enemy")
        assertEquals(200, responseFindEnemy.statusCode)

        println(responseFindEnemy.body.print())
        val playerSearchDto = responseFindEnemy.`as`(PlayerSearchDto::class.java)

        // fight
        val responseFigth = RestAssured.given().contentType(ContentType.JSON)
                .cookie("SESSION", cookie2.session)
                .header("X-XSRF-TOKEN", cookie2.csrf)
                .cookie("XSRF-TOKEN", cookie2.csrf)
                .body(playerSearchDto)
                .post("/api/v1/gamelogic-server/play/fight")

        assertEquals(200, responseFigth.statusCode)
        // check that username of player is present in fight log
        assertTrue(responseFigth.body.print().contains(id2))


        // match result persisted (checking rabbitMQ) that match was persisted
        await().atMost(20, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until({
                    RestAssured.given().cookie("SESSION", cookie2.session)
                            .get("/api/v1/player-server/players")
                            .then()
                            .statusCode(200)
                            .and()
                            .body("username", CoreMatchers.hasItem(id2))
                    true
                })
    }

    private fun createUniqueId(): String {
        counter++
        return "foo_${counter}"
    }

    class NeededCookies(val session:String, val csrf: String)

    private fun registerUser(id: String, password: String): NeededCookies {

        val xsrfToken = RestAssured.given().contentType(ContentType.URLENC)
                .formParam("the_user", id)
                .formParam("the_password", password)
                .post("/api/v1/signIn")
                .then()
                .statusCode(403)
                .extract().cookie("XSRF-TOKEN")

        val session =  RestAssured.given().contentType(ContentType.URLENC)
                .formParam("the_user", id)
                .formParam("the_password", password)
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookie("XSRF-TOKEN", xsrfToken)
                .post("/api/v1/signIn")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        return NeededCookies(session, xsrfToken)
    }


}
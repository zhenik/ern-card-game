package no.ern.game.e2etests

import org.testcontainers.containers.DockerComposeContainer
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
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
        val id = "guy"
        val cookie1 = registerUser(id, "password")

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
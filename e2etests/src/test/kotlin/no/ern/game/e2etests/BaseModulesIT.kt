package no.ern.game.e2etests

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit

class BaseModulesIT {
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


            Awaitility.await().atMost(240, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until({
                        RestAssured.given().get("http://localhost:10000/api/v1/user").then().statusCode(401)

                        true
                    })
        }
    }


    @Test
    fun testUnauthorizedAccess() {
        RestAssured.given().get("/api/v1/user")
                .then()
                .statusCode(401)
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

    private fun createUniqueId(): String {
        counter++
        return "foo_$counter"
    }


    @Test
    fun testLogin() {
        val id = createUniqueId()
        val pwd = "bar"

        val cookies = registerUser(id, pwd)

        // no access
        RestAssured.given().get("/api/v1/item-server/items").then().statusCode(401)
        //with acces
        RestAssured.given().cookie("SESSION", cookies.session)
                .get("/api/v1/item-server/items")
                .then()
                .statusCode(200)

        // no access
        RestAssured.given().get("/api/v1/player-server/players").then().statusCode(401)
        //with acces
        RestAssured.given().cookie("SESSION", cookies.session)
                .get("/api/v1/player-server/players")
                .then()
                .statusCode(200)

        // no access
        RestAssured.given().get("/api/v1/match-server/matches").then().statusCode(401)
        //with acces
        RestAssured.given().cookie("SESSION", cookies.session)
                .get("/api/v1/match-server/matches")
                .then()
                .statusCode(200)

        // Trying to make post request to matches
        RestAssured.given()
                .cookie("SESSION", cookies.session)
                .contentType(ContentType.JSON)
                .header("X-XSRF-TOKEN", cookies.csrf)
                .cookie("XSRF-TOKEN", cookies.csrf)
                .post("/api/v1/match-server/matches")
                .then()
                .statusCode(403)

        // test RabbitMq(when create user -> send message to player module and create player for that user)
        await().atMost(10, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until({
                    RestAssured.given().cookie("SESSION", cookies.session)
                            .get("/api/v1/player-server/players")
                            .then()
                            .statusCode(200)
                            .and()
                            .body("username", CoreMatchers.hasItem(id))
                    true
                })
    }
}

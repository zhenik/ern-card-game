//package no.ern.game.e2etests
//
//import io.restassured.RestAssured
//import io.restassured.RestAssured.given
//import io.restassured.http.ContentType
//import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
//import org.awaitility.Awaitility.await
//import org.hamcrest.CoreMatchers
//import org.hamcrest.CoreMatchers.*
//import org.hamcrest.Matchers.contains
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.BeforeClass
//import org.junit.ClassRule
//import org.junit.Test
//import org.testcontainers.containers.DockerComposeContainer
//import java.io.File
//import java.util.concurrent.TimeUnit
//
//
//class GameLogicIT {
//
//    companion object {
//
//        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)
//
//
//        @ClassRule
//        @JvmField
//        val env = KDockerComposeContainer(File("../docker-compose.yml"))
//                .withLocalCompose(true)
//
//        private var counter = System.currentTimeMillis()
//
//        @BeforeClass
//        @JvmStatic
//        fun initialize() {
//            RestAssured.baseURI = "http://localhost"
////            RestAssured.port = 80
//            RestAssured.port = 10000
//            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
//
//
//            await().atMost(180, TimeUnit.SECONDS)
//                    .ignoreExceptions()
//                    .until({
//                        // check GATEWAY is available
//
//                        given().get("http://localhost:10000/api/v1/user").then().statusCode(401)
//
////                        RestAssured.given().get("http://localhost:10000/api/v1/user").then().statusCode(401)
////                        // check GAMELOGIC is available
////                        RestAssured.given().get("/api/v1/gamelogic-server/play/enemy")
////                                .then()
////                                .statusCode(401)
//
////                        RestAssured.given().get("/api/v1/player-server/players")
////                                .then()
////                                .statusCode(401)
//
////                        RestAssured.given().get("/api/v1/player-server/players")
////                                .then()
////                                .statusCode(200)
//
//                        true
//                    })
//        }
//    }
//
////    @Before
////    fun checkEureka(){
////        await().atMost(60, TimeUnit.SECONDS)
////                .ignoreExceptions()
////                .until({
////                    given()
////                            .get("http://localhost:8761/eureka/apps")
////                            .then()
////                            .statusCode(200)
////                            .body("applications.application.instance.size()", equalTo(3))
////                    true
////                })
////    }
//
////    @Test
////    fun checkThatWoman(){
////        await().atMost(60, TimeUnit.SECONDS)
////                .ignoreExceptions()
////                .until({
////                    given()
////                            .get("http://localhost:8761/eureka/apps")
////                            .then()
////                            .statusCode(200)
////                            .body("applications.application.instance.size()", equalTo(3))
////                    true
////                })
////    }
//
////    @Test
////    fun test(){
////        RestAssured.given().get("/api/v1/player-server/players")
////                .then()
////                .statusCode(200)
////    }
////
////
//    @Test
//    fun testUnauthorizedAccess() {
//        RestAssured.given().get("/api/v1/gamelogic-server/play/enemy")
//                .then()
//                .statusCode(401)
//
//        RestAssured.given().get("/api/v1/gamelogic-server/play/fight")
//                .then()
//                .statusCode(401)
//    }
//
////    TODO: test /gamelogic/username 200OK
//    @Test
//    fun findEnemyAndFight() {
//        // Arrange
//        val id = "guy"
//        val cookie1 = registerUser(id, "password")
//
//
//
//        // Act
////        given().cookie("SESSION", cookie1.session)
////                .get("/api/v1/gamelogic-server/play/enemy")
////                .then()
////                .statusCode(404)
//
////        RestAssured.given()
////                .cookie("SESSION", cookie1.session)
////                .get("/api/v1/gamelogic-server/play/test")
////                .then()
////                .statusCode(200)
//
//
//        await().atMost(30, TimeUnit.SECONDS)
//                .ignoreExceptions()
//                .until({
//                    RestAssured.given()
//                        .cookie("SESSION", cookie1.session)
//                        .get("/api/v1/gamelogic-server/play/test")
//                        .then()
//                        .statusCode(200)
//                    true
//                })
//
//
//
////        assertTrue(true)
//
//
//
////        RestAssured.given()
////                .cookie("SESSION", cookie1.session)
//////                .header("X-XSRF-TOKEN", cookie1.csrf)
//////                .cookie("XSRF-TOKEN", cookie1.csrf)
////                .get("/api/v1/gamelogic-server/play/test")
////                .then()
////                .statusCode(200)
//
//        val fakeEnemy = PlayerSearchDto("134","oda",1)
//        val fakeEnemyUrself = PlayerSearchDto("1","guy",1)
//
//        // send empty body
////        given().cookie("SESSION", cookie1.session)
////                .header("X-XSRF-TOKEN", cookie1.csrf)
////                .cookie("XSRF-TOKEN", cookie1.csrf)
////                .body(PlayerSearchDto())
////                .post("/api/v1/gamelogic-server/play/fight")
////                .then()
////                .statusCode(400)
//
//        // send enemy which is not exist
////        given().cookie("SESSION", cookie1.session)
////                .header("X-XSRF-TOKEN", cookie1.csrf)
////                .cookie("XSRF-TOKEN", cookie1.csrf)
////                .body(fakeEnemy)
////                .post("/api/v1/gamelogic-server/play/fight")
////                .then()
////                .statusCode(404)
//
//        // try to fight urself
////        given().cookie("SESSION", cookie1.session)
////                .header("X-XSRF-TOKEN", cookie1.csrf)
////                .cookie("XSRF-TOKEN", cookie1.csrf)
////                .body(fakeEnemyUrself)
////                .post("/api/v1/gamelogic-server/play/fight")
////                .then()
////                .statusCode(404)
////
//////                .body("name", equalTo(id))
//////                .body("roles", contains("ROLE_USER"))
//    }
//
//    class NeededCookies(val session:String, val csrf: String)
//
//    private fun registerUser(id: String, password: String): NeededCookies {
//
//        val xsrfToken = RestAssured.given().contentType(ContentType.URLENC)
//                .formParam("the_user", id)
//                .formParam("the_password", password)
//                .post("/api/v1/signIn")
//                .then()
//                .statusCode(403)
//                .extract().cookie("XSRF-TOKEN")
//
//        val session =  RestAssured.given().contentType(ContentType.URLENC)
//                .formParam("the_user", id)
//                .formParam("the_password", password)
//                .header("X-XSRF-TOKEN", xsrfToken)
//                .cookie("XSRF-TOKEN", xsrfToken)
//                .post("/api/v1/signIn")
//                .then()
//                .statusCode(204)
//                .extract().cookie("SESSION")
//
//        return NeededCookies(session, xsrfToken)
//    }
//
//    private fun createUniqueId(): String {
//        counter++
//        return "foo_$counter"
//    }
////
////
////    @Test
////    fun testLogin() {
////
////        val id = createUniqueId()
////        val pwd = "bar"
////
////        val cookies = registerUser(id, pwd)
////
////        RestAssured.given().get("/api/v1/user")
////                .then()
////                .statusCode(401)
////
////        //note the difference in cookie name
////        RestAssured.given().cookie("SESSION", cookies.session)
////                .get("/api/v1/user")
////                .then()
////                .statusCode(200)
////                .body("name", CoreMatchers.equalTo(id))
////                .body("roles", Matchers.contains("ROLE_USER"))
////
////
////        RestAssured.given().auth().basic(id, pwd)
////                .get("/api/v1/user")
////                .then()
////                .statusCode(200)
////                .cookie("SESSION")
////                .body("name", CoreMatchers.equalTo(id))
////                .body("roles", Matchers.contains("ROLE_USER"))
////    }
//
////    @Test
////    fun
////
////    @Test
////    fun testOpenCount(){
////
////        val x = given().basePath("/user-service/usersInfoCount")
////                .get()
////                .then()
////                .statusCode(200)
////                .extract().body().asString().toInt()
////
////        assertTrue(x >= 0 )
////    }
////
////
////
////    @Test
////    fun testForbiddenToChangeOthers() {
////
////        val firstId = createUniqueId()
////        val firstCookies = registerUser(firstId, "123")
////        val firstPath = "/user-service/usersInfo/$firstId"
////
////        /*
////            In general, it can make sense to have the DTOs in their
////            own module, so can be reused in the client directly.
////            Otherwise, we would need to craft the JSON manually,
////            as done in these tests
////         */
////
////        given().cookie("SESSION", firstCookies.session)
////                .get("/user")
////                .then()
////                .statusCode(200)
////                .body("name", equalTo(firstId))
////                .body("roles", contains("ROLE_USER"))
////
////
////        given().cookie("SESSION", firstCookies.session)
////                .cookie("XSRF-TOKEN", firstCookies.csrf)
////                .header("X-XSRF-TOKEN", firstCookies.csrf)
////                .contentType(ContentType.JSON)
////                .body("""
////                    {
////                        "userId": "$firstId",
////                        "name": "A",
////                        "surname": "B",
////                        "email": "a@a.com"
////                    }
////                    """)
////                .put(firstPath)
////                .then()
////                .statusCode(201)
////
////
////        val secondId = createUniqueId()
////        val secondCookies = registerUser(secondId, "123")
////        val secondPath = "/user-service/usersInfo/$secondId"
////
////        given().cookie("SESSION", secondCookies.session)
////                .cookie("XSRF-TOKEN", secondCookies.csrf)
////                .header("X-XSRF-TOKEN", secondCookies.csrf)
////                .contentType(ContentType.JSON)
////                .body("""
////                    {
////                        "userId": "$secondId",
////                        "name": "bla",
////                        "surname": "bla",
////                        "email": "bla@bla.com"
////                    }
////                    """)
////                .put(secondPath)
////                .then()
////                .statusCode(201)
////
////
////
////        given().cookie("SESSION", firstCookies.session)
////                .cookie("XSRF-TOKEN", firstCookies.csrf)
////                .header("X-XSRF-TOKEN", firstCookies.csrf)
////                .contentType(ContentType.JSON)
////                .body("""
////                    {
////                        "userId": "$secondId"
////                    }
////                    """)
////                .put(secondPath)
////                .then()
////                .statusCode(403)
////    }
////
////
////    @Test
////    fun testGetGreetings(){
////
////        val id = createUniqueId()
////        val name = "foo"
////        val pwd = "bar"
////
////        val cookies = registerUser(id, pwd)
////
////
////        given().cookie("SESSION", cookies.session)
////                .cookie("XSRF-TOKEN", cookies.csrf)
////                .header("X-XSRF-TOKEN", cookies.csrf)
////                .contentType(ContentType.JSON)
////                .body("""
////                    {
////                        "userId": "$id",
////                        "name": "$name",
////                        "surname": "B",
////                        "email": "a@a.com"
////                    }
////                    """)
////                .put("/user-service/usersInfo/$id")
////                .then()
////                .statusCode(201)
////
////
////        given().cookie("SESSION", cookies.session)
////                .accept(ContentType.JSON)
////                .get("/greetings/api/$id")
////                .then()
////                .statusCode(200)
////                .body("message", startsWith("Hello"))
////                .body("message", containsString(name))
////
////    }
//}

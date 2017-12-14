package no.ern.game.gamelogic.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ern.game.schema.dto.gamelogic.FightResultLogDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import org.junit.Assert.*
import org.junit.Test

class GameLogicControllerTest : ControllerTestBase() {



    @Test
    fun testPathNotExist() {
        RestAssured.given().get("/endpointThatDoesNotExist")
                .then()
                .statusCode(401)
    }

    @Test
    fun testGetUsernameEndpoint() {
        val response = given()
                .auth().basic("foo", "123")
                .get("/username")

        println("response print: " + response.print())
    }

    @Test
    fun testFindOpponent_Valid() {
        // Mock player json
        val json = getMockedJson_FooAndBar()
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(json)))

        val response = given()
                .auth().basic("foo", "123")
                .accept(ContentType.JSON)
                .get("/enemy")

        //println(response.body.print())

        val playerSearchDto = response.`as`(PlayerSearchDto::class.java)

        assertEquals(200, response.statusCode)
        assertEquals("bar", playerSearchDto.username)
        assertEquals("2", playerSearchDto.id)
        assertEquals(1, playerSearchDto.level)
    }

    @Test
    fun testFindOpponent_NotPlayersFound() {
        // Arrange
        val json = getMockedJson_EmptyArray()
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(json)))

        // Act
        val response = RestAssured
                .given()
                .auth().basic("foo", "123")
                .accept(ContentType.JSON).get("/enemy")


        // Assert
        assertEquals(404, response.statusCode)
    }

    @Test
    fun fight_TryFightYourself(){

        // Try to fight yourself

        // Arrange
        val invalidPlayerSearchDto = PlayerSearchDto("1", "foo")
        val json = getMockedJson_FooByUsername() // This should ONLY return yourself, because of query

        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlPathMatching(".*/players.*"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(json)))

        // Act
        val response = RestAssured
                .given()
                .auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(invalidPlayerSearchDto)
                .post("/fight")

        // Act
        assertEquals(400, response.statusCode)
    }


    @Test
    fun fight_OnePlayerNotFound() {

        // Arrange
        val player1Dto = getPlayerDto("2", "bar","")
        val playerSearchDto = PlayerSearchDto("2","bar")

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(player1Dto)))
        // NOT found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withStatus(404)))

        // Act
        val response = RestAssured
                .given()
                .auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playerSearchDto)
                .post("/fight")

        // Assert
        assertEquals(404, response.statusCode)
    }

    @Test
    fun fight_NoItems_Success() {

        // Arrange
        val fooJson = getMockedJson_FooByUsername()
        val barJson = getMockedJson_BarById()
        val playerSearchDto = PlayerSearchDto("2","bar")

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*foo"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(fooJson)))
        // found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(barJson)))

        // new match result created
        wiremockServerMatch.stubFor(
                WireMock.post(
                        urlMatching(".*/matches"))
                        .willReturn(WireMock.aResponse().withStatus(201)))

        // Act
        val response = RestAssured
                .given()
                .auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playerSearchDto)
                .post("/fight")

        // get fight log
        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)


        // Assert
        assertEquals(200, response.statusCode)
        assertTrue(fightResultLogDto.winner=="foo" || fightResultLogDto.winner=="bar")
    }

    @Test
    fun fight_WithItems_Success() {
        // Arrange
        val query1 = "1"
        val query2 = "1,2"

        val fooJson =
        """
        [
            {
                "username": "foo",
                "id": "1",
                "health": 100,
                "damage": 10,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": [1]
            }
        ]
        """
        val barJson =
        """

            {
                "username": "bar",
                "id": "2",
                "health": 209,
                "damage": 10,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": [1,2]
            }

        """
        val playersFightIdsDto = PlayerSearchDto("2","bar")

        val items1 = getJsonOneItems()
        val items2 = getJsonTwoItems()

        // mock player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*foo"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(fooJson)))
        // mock player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(barJson)))

        // items mock for player 1 (I can't use `?` because its spec symbol in regEx)
        wiremockServerItem.stubFor(
                WireMock.get(
//                        urlMatching(".*1"))
                        urlMatching(".*ids=1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items1))
        )
        // items mock for player 2
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*ids=1,2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items2))
        )



        // new match result created
        wiremockServerMatch.stubFor(
                WireMock.post(
                        urlMatching(".*/matches"))
                        .willReturn(WireMock.aResponse().withStatus(201)))


        // Act
        val response = RestAssured
                .given()
                .auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")

        // get fight log
        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)


        // Assert
        assertEquals(200, response.statusCode)
        assertTrue(fightResultLogDto.winner=="foo" || fightResultLogDto.winner=="bar")
        // Base damage for both players 15 (10 + 5 (items bonus) )
        assertTrue(fightResultLogDto.toString().contains("[15]"))
    }


    // items present on player, but have not found on item module

    @Test
    fun fight_WithItems_ItemsConflict() {
        // Arrange
        val query1 = "1"
        val query2 = "1,2"
        val playerDto1 = getPlayerDto("1", "name1", query1)
        val playerDto2 = getPlayerDto("2", "name2", query2)
        val playersFightIdsDto =
        val items1 = getJsonOneItems()

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto1)))
        // found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto2)))

        // items found for player 1 (I can't use `?` because its spec symbol in regEx)
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items1))
        )
        // items found for player 2
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1,2"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(404))
        )


        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")

        // Assert
        assertEquals(404, response.statusCode)

    }


//    @Test
//    fun testFindOpponent_Valid() {
//        // Arrange
//        val json = getMockedJson_PlayerSearch()
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players"))
//                            .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(json)))
//
//        // Act
//        val response = RestAssured.given().accept(ContentType.JSON).get("/enemy")
//        val playerSearchDto = response.`as`(PlayerSearchDto::class.java)
//
//        // Assert
//        assertEquals(200, response.statusCode)
//        assertEquals("name", playerSearchDto.username)
//        assertEquals("1", playerSearchDto.id)
//        assertEquals(1, playerSearchDto.level)
//    }
//
//
//    @Test
//    fun testFindOpponent_NotPlayersFound() {
//        // Arrange
//        val json = getMockedJson_EmptyArray()
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(json)))
//
//        // Act
//        val response = RestAssured.given().accept(ContentType.JSON).get("/enemy")
//
//
//        // Assert
//        assertEquals(404, response.statusCode)
//    }
//
//
//    /**
//     * FIGHT cases
//     * */
//
//    @Test
//    fun fight_GivenPayloadInvalid(){
//
//        // Arrange
//        val invalidFigthIdsDto = PlayersFightIdsDto("1","1")
//
//        // Act
//        val response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .body(invalidFigthIdsDto)
//                .post("/fight")
//
//        // Act
//        assertEquals(400, response.statusCode)
//
//    }
//
//    @Test
//    fun fight_OnePlayerNotFound() {
//
//        // Arrange
//        val player1Dto = getPlayerDto("1", "name1","")
//        val playersFightIdsDto = PlayersFightIdsDto("1","2")
//
//        // found player 1
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(player1Dto)))
//        // NOT found player 2
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/2"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withStatus(404)))
//
//        // Act
//        val response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .body(playersFightIdsDto)
//                .post("/fight")
//
//        // Assert
//        assertEquals(404, response.statusCode)
//    }
//
//    @Test
//    fun fight_NoItems_Success() {
//
//        // Arrange
//        val playerDto1 = getPlayerDto("1", "name1", "")
//        val playerDto2 = getPlayerDto("2", "name2", "")
//        val playersFightIdsDto = PlayersFightIdsDto("1","2")
//
//        // found player 1
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto1)))
//        // found player 2
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/2"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto2)))
//
//        // new match result created
////        wiremockServerMatch.stubFor(
////                WireMock.post(
////                        urlMatching(".*/matches"))
////                        .willReturn(WireMock.aResponse().withStatus(201)))
//
//
//
//        // Act
//        val response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .body(playersFightIdsDto)
//                .post("/fight")
//
//        println("HERE MAZZAFAKKA")
//        println(response.body.print())
//        // get fight log
//        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)
//
//
//        // Assert
//        assertEquals(200, response.statusCode)
//        assertTrue(fightResultLogDto.winner=="name1" || fightResultLogDto.winner=="name2")
//    }
//
//    @Test
//    fun fight_WithItems_Success() {
//        // Arrange
//        val query1 = "1"
//        val query2 = "1,2"
//        val playerDto1 = getPlayerDto("1", "name1", query1)
//        val playerDto2 = getPlayerDto("2", "name2", query2)
//        val playersFightIdsDto = PlayersFightIdsDto("1","2")
//        val items1 = getJsonOneItems()
//        val items2 = getJsonTwoItems()
//
//        // found player 1
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto1)))
//        // found player 2
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/2"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto2)))
//
//        // items found for player 1 (I can't use `?` because its spec symbol in regEx)
//        wiremockServerItem.stubFor(
//                WireMock.get(
//                        urlMatching(".*1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(items1))
//        )
//        // items found for player 2
//        wiremockServerItem.stubFor(
//                WireMock.get(
//                        urlMatching(".*1,2"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(items2))
//        )
//
//
//
//        // new match result created
////        wiremockServerMatch.stubFor(
////                WireMock.post(
////                        urlMatching(".*/matches"))
////                        .willReturn(WireMock.aResponse().withStatus(201)))
//
//
//        // Act
//        val response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .body(playersFightIdsDto)
//                .post("/fight")
//
//        // get fight log
//        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)
//
//
//        // Assert
//        assertEquals(200, response.statusCode)
//        assertTrue(fightResultLogDto.winner=="name1" || fightResultLogDto.winner=="name2")
//        // player 2 total life is 112 (because of items bonus), and base damage for both players 15 (10 + 5 (items bonus) )
//        assertTrue(fightResultLogDto.toString().contains("/112") && fightResultLogDto.toString().contains("[15]"))
//    }
//
//    // items present on player, but have not found on item module
//    @Test
//    fun fight_WithItems_ItemsConflict() {
//        // Arrange
//        val query1 = "1"
//        val query2 = "1,2"
//        val playerDto1 = getPlayerDto("1", "name1", query1)
//        val playerDto2 = getPlayerDto("2", "name2", query2)
//        val playersFightIdsDto = PlayersFightIdsDto("1","2")
//        val items1 = getJsonOneItems()
//
//        // found player 1
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto1)))
//        // found player 2
//        wiremockServerPlayer.stubFor(
//                WireMock.get(
//                        urlMatching(".*/players/2"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(playerDto2)))
//
//        // items found for player 1 (I can't use `?` because its spec symbol in regEx)
//        wiremockServerItem.stubFor(
//                WireMock.get(
//                        urlMatching(".*1"))
//                        .willReturn(WireMock.aResponse()
//                                .withHeader("Content-Type", "application/json; charset=utf-8")
//                                .withBody(items1))
//        )
//        // items found for player 2
//        wiremockServerItem.stubFor(
//                WireMock.get(
//                        urlMatching(".*1,2"))
//                        .willReturn(WireMock.aResponse()
//                                .withStatus(404))
//        )
//
//
//        // Act
//        val response = RestAssured
//                .given()
//                .contentType(ContentType.JSON)
//                .accept(ContentType.JSON)
//                .body(playersFightIdsDto)
//                .post("/fight")
//
//        // Assert
//        assertEquals(404, response.statusCode)
//
//    }


}
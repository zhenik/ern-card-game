package no.ern.game.player.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import io.restassured.RestAssured
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.PlayerDto
import org.junit.Test

class PlayerControllerWiremockTest : WiremockTestBase() {

    @Test
    fun testAddItemToPlayer_Valid() {

        val playerDto1 = PlayerDto(
                "1",
                "Bob",
                null,
                120,
                44,
                30,
                40,
                1,
                listOf()
        )
        val savedId = RestAssured.given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)

        wiremockServerItem.stubFor(
                WireMock.get(urlMatching(".*/items/1"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)))

        val item = ItemDto(id = "1")

        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(item)
                .post("/$savedId/items")

        print(response.print())
        assertEquals(200, response.statusCode)
    }

    @Test
    fun testAddItemToPlayer_Invalid() {
        val playerDto1 = PlayerDto(
                "1",
                "Bob",
                null,
                120,
                44,
                30,
                40,
                1,
                listOf(4L)
        )
        val item = ItemDto(id = "1")
        val savedId = RestAssured.given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)

        // Stub 200, item with 1 should exist
        wiremockServerItem.stubFor(
                WireMock.get(urlMatching(".*/items/1"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)))

        // Try to add item to non-existant user.
        val responseInvalidUserId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(item)
                .post("/4/items")
                .then()
                .statusCode(404)



        // Add item with id that does not exist
        val itemWithInvalidId = ItemDto(id = "2")
        wiremockServerItem.stubFor(
                WireMock.get(urlMatching(".*/items/2"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(404)))
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(itemWithInvalidId)
                .post("/$savedId/items")
                .then()
                .statusCode(404)

        // Try to add item without an id
        val itemWithoutId = ItemDto(id = "")
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(itemWithoutId)
                .post("/$savedId/items")
                .then()
                .statusCode(404)

        // Try to add item with text as an id
        val itemWithTextId = ItemDto(id = "asdasd")
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(itemWithTextId)
                .post("/$savedId/items")
                .then()
                .statusCode(404)

        // Try to add same item twice
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(item)
                .post("/$savedId/items")
                .then()
                .statusCode(200)
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(item)
                .post("/$savedId/items")
                .then()
                .statusCode(400)
    }
}
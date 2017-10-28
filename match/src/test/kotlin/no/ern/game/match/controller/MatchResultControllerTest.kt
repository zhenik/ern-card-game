package no.ern.game.match.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath.from
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchResultControllerTest : ControllerTestBase(){



    @Test
    fun testCleanDB() {
        RestAssured.given().get().then()
                .statusCode(200)
                .body("size()", CoreMatchers.equalTo(0))
    }


    @Test
    fun testCreateAndGet() {
        // Arrange
        val dto = getValidMatchResultDto()

        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get().then().statusCode(200).body("size()", CoreMatchers.equalTo(0))

        // post
        val id = RestAssured.given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().asString()
        // get
        RestAssured.given().get().then().statusCode(200).body("size()", CoreMatchers.equalTo(1))

        // get :id
        val json = RestAssured.given().pathParam("id", id)
                .get("/{id}")
                .andReturn()
                .asString()

        assertEquals(from(json).getLong("attacker.health"), dto.attacker!!.health!!)
        assertEquals(from(json).getLong("attacker.remainingHealth"), dto.attacker!!.remainingHealth!!)

    }
}
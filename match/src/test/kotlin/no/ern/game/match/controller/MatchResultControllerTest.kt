package no.ern.game.match.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath.from
import no.ern.game.match.domain.dto.MatchResultDto
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.Assert.*
import javax.smartcardio.Card



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
        val listDtos = RestAssured.given().get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))
                .extract()
                .`as`(Array<MatchResultDto>::class.java)
                .toList()

        assertEquals(1, listDtos.size)

        assertTrue(listDtos.stream().anyMatch{dto.attacker!!.username!! == (it.attacker!!.username!!)})


    }
}
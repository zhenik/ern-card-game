package no.ern.game.item.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ern.game.item.domain.dto.ItemDto
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class MatchResultControllerTest : ICTestBase() {
    @Test
    fun testCleanDB() {
        RestAssured.given().get().then()
                .statusCode(200)
                .body("size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCreateItem(){
        // POST /matches
        var sword = getItemDto()

        // valid dto
        val id = RestAssured.given().contentType(ContentType.JSON)
                .body(sword)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)
        Assert.assertNotNull(id)

        // invalid dto
        RestAssured.given().contentType(ContentType.JSON)
                .body("5318008")
                .post()
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetItems(){
        val item = getItemDto()
        val id = postNewItem(item)

        // GET ../items
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

        // -->Our item is a Weapon and has level 2 as levelRequirement<--

        // GET ../items?type=Weapon
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("type", item.type)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

        // GET ../items?type=woof    <-- Invalid enum type!
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("type", "woof")
                .get().then().statusCode(400)

        // GET ../items?minLevel=0&maxLevel=2
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("minLevel", 0)
                .param("maxLevel", 2)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

        // GET ../items?minLevel=3&maxLevel=5    <-- Our item is outside of this range!
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("minLevel", 3)
                .param("maxLevel", 5)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(0))

        // GET ../items?minLevel=0&maxLevel=2&type=Weapon    <-- Our item falls within our specified parameters :)
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("minLevel", 0)
                .param("maxLevel", 2)
                .param("type", "Weapon")
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

    }

    @Test
    fun testGetItem(){
        // Arrange
        val item = getItemDto()
        val id = postNewItem(item)

        // GET /items/:id
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ItemDto::class.java)
        Assert.assertTrue(item.name == response.name)

        //GET /items/somethingInvalid
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id","text")
                .get("/{id}")
                .then()
                .statusCode(400)

        // not found in db
        //GET /items/5318008
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",5318008)
                .get("{id}")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteMatchResult(){
        val itemDto = getItemDto()
        val id = postNewItem(itemDto)

        // GET /items/:id
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ItemDto::class.java)
        Assert.assertTrue(itemDto.name == response.name)

        // DELETE /items/somethingInvalid
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id","text")
                .delete("/{id}")
                .then()
                .statusCode(400)

        // DELETE /items/:id
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .delete("/{id}")
                .then()
                .statusCode(204)

        //item has been deleted from db and can no longer be found
        //GET /items/:id
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("{id}")
                .then()
                .statusCode(404)
    }

    @Test
    fun testReplaceItem(){
        // Arrange
        val itemDto = getItemDto()
        val id = postNewItem(itemDto)

        // GET /items/:id
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ItemDto::class.java)
        Assert.assertTrue(itemDto.name == response.name)

        //change the name and set ID to match the one generated
        itemDto.name = "blade"
        itemDto.id = id.toString()

        //PUT items/:id
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .body(itemDto)
                .put("/{id}")
                .then()
                .statusCode(204)

        // GET /items/:id
        val response2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ItemDto::class.java)
        // The name has changed from our PUT
        Assert.assertTrue(itemDto.name == response2.name)

        // The ID is still the same!
        Assert.assertTrue(response2.id == response.id)
    }

    @Test
    fun testUpdateItemName() {
        // Arrange
        val itemDto = getItemDto()
        val id = postNewItem(itemDto)
        val newName = "blade"

        Assert.assertNotEquals(itemDto.name, newName)


        RestAssured.given()
                .pathParam("id", id)
                .body(newName)
                .patch("/{id}")
                .then()
                .statusCode(204)

        // GET /items/:id
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ItemDto::class.java)

        // The name of the item has now been changed after patch
        Assert.assertTrue(newName == response.name)

    }
}
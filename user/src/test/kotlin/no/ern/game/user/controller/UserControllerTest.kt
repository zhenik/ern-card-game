package no.ern.game.user.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.TestCase.assertNotNull
import no.ern.game.user.domain.dto.UserDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserControllerTest : TestBase() {

    @Before
    fun assertThatDatabaseIsEmpty(){
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    @Test
    fun createAndGetUserByUsername() {
        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]

        postUserDto(userDto1, 201)
        postUserDto(userDto2, 201)

        given().get().then().statusCode(200).body("size()", equalTo(2))

        given().pathParam("username", userDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto1.username))
                .body("password", equalTo(userDto1.password))
                .body("experience", equalTo(userDto1.experience))

        given().pathParam("username", userDto2.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto2.username))
                .body("password", equalTo(userDto2.password))
                .body("experience", equalTo(userDto2.experience))
    }

    @Test
    fun createUsersWithDuplicateUsername() {
        val userDto1 = getValidUserDtos()[0]

        postUserDto(userDto1, 201)
        postUserDto(userDto1, 400)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun createUserWithIdFails() {
        val userDto = getValidUserDtos()[0]
        userDto.id = "12312312"

        postUserDto(userDto, 400)

        given().get().then().statusCode(200).body("size()", equalTo(0))

    }

    @Test
    fun getAllUsersByLevel() {
        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]
        val unusedLevel = getValidUserDtos()[2].level

        postUserDto(userDto1, 201)
        postUserDto(userDto2, 201)

        given().get().then().statusCode(200).body("size()", equalTo(2))


        given().param("level", userDto1.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", userDto2.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", unusedLevel)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))

        //Why does this get all fields as an "array" ??? [username] instead of username
        val foundUser = given().contentType(ContentType.JSON)
                .param("level", userDto1.level)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<UserDto>::class.java)
        assertEquals(foundUser[0].username, userDto1.username)
        assertEquals(foundUser[0].password, userDto1.password)
        assertEquals(foundUser[0].experience, userDto1.experience)
    }

    @Test
    fun updateUser() {
        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]

        val postedId = postUserDto(userDto1, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))
        assertNotNull(postedId)
        userDto2.id = postedId.toString()

        // Update data to be like userDto2
        given().pathParam("id", postedId)
                .contentType(ContentType.JSON)
                .body(userDto2)
                .put("/{id}")
                .then()
                .statusCode(204)

        // Validate that it changed
        given().pathParam("username", userDto2.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto2.username))
                .body("password", equalTo(userDto2.password))
                .body("health", equalTo(userDto2.health))
                .body("experience", equalTo(userDto2.experience))
                .body("id", equalTo(postedId.toString()))

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun updateUserChangedId() {
        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]

        val postedId = postUserDto(userDto1, 201)
        assertNotNull(postedId)

        // Change ID in dto, but not path.
        userDto2.id = (postedId?.times(2)).toString()

        given().pathParam("id", postedId)
                .contentType(ContentType.JSON)
                .body(userDto2)
                .put("/{id}")
                .then()
                .statusCode(409)

        given().pathParam("username", userDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto1.username))
                .body("password", equalTo(userDto1.password))
                .body("health", equalTo(userDto1.health))
                .body("experience", equalTo(userDto1.experience))

        given().pathParam("username", userDto2.username)
                .get("/{username}")
                .then()
                .statusCode(404)
    }

    @Test
    fun deleteUserByUsername() {
        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]

        postUserDto(userDto1, 201)
        postUserDto(userDto2, 201)
        given().get().then().statusCode(200).body("size()", equalTo(2))


        given().pathParam("username", userDto1.username)
                .delete("/{username}")
                .then()
                .statusCode(204)
        given().get().then().statusCode(200).body("size()", equalTo(1))

        given().pathParam("username", userDto2.username)
                .delete("/{username}")
                .then()
                .statusCode(204)
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    @Test
    fun deleteUserWithEmptyUsername() {
        val userDto1 = getValidUserDtos()[0]
        postUserDto(userDto1, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))

        // Since delete on /game/api/users is not supported, return a 405: Method Not Allowed.
        // Delete is only allowed on /game/api/users/{username}
        given().pathParam("username", "")
                .delete("/{username}")
                .then()
                .statusCode(405)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun deleteUserWithNonExistingUsername() {
        val userDto1 = getValidUserDtos()[0]
        postUserDto(userDto1, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))
        given().pathParam("username", "golang")
                .delete("/{username}")
                .then()
                .statusCode(404)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    private fun postUserDto(userDto2: UserDto, expectedStatusCode: Int): Long? {
        return try {
            given().contentType(ContentType.JSON)
                    .body(userDto2)
                    .post()
                    .then()
                    .statusCode(expectedStatusCode)
                    .extract().`as`(Long::class.java)
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun getValidUserDtos(): List<UserDto> {
        return listOf(
                UserDto(
                        null,
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
                        30,
                        40,
                        1,
                        listOf()
                ),
                UserDto(
                        null,
                        "Kotlin",
                        "Spicy language..",
                        "Thisshouldalsobesalted",
                        122,
                        46,
                        33,
                        47,
                        23,
                        listOf()
                ),
                UserDto(
                        null,
                        "Another language",
                        "Its actually PHP...",
                        "yes, there should be salt here",
                        132,
                        56,
                        38,
                        68,
                        68,
                        listOf()
                )
        )
    }

}
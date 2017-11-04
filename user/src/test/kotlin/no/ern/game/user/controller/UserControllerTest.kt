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

        val foundUser1 = given().contentType(ContentType.JSON)
                .pathParam("username", userDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(UserDto::class.java)

        assertEquals(userDto1.username, foundUser1.username)
        assertEquals(userDto1.password, foundUser1.password)
        assertEquals(userDto1.health, foundUser1.health)
        assertEquals(userDto1.equipment, foundUser1.equipment)

        val foundUser2 = given().contentType(ContentType.JSON)
                .pathParam("username", userDto2.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(UserDto::class.java)

        assertEquals(userDto2.username, foundUser2.username)
        assertEquals(userDto2.password, foundUser2.password)
        assertEquals(userDto2.health, foundUser2.health)
        assertEquals(userDto2.equipment, foundUser2.equipment)
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

        val foundUser = given().contentType(ContentType.JSON)
                .pathParam("username", userDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(UserDto::class.java)

        assertEquals(userDto1.username, foundUser.username)
        assertEquals(userDto1.password, foundUser.password)
        assertEquals(userDto1.health, foundUser.health)
        assertEquals(userDto1.equipment, foundUser.equipment)




        given().pathParam("username", userDto2.username)
                .get("/{username}")
                .then()
                .statusCode(404)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun setUsername() {
        val userDto1 = getValidUserDtos()[0]
        val newUsername = "newUsername"

        val postedId = postUserDto(userDto1, 201)

        given().pathParam("id", postedId)
                .body(newUsername)
                .patch("/{id}")
                .then()
                .statusCode(204)


        val foundUser = given().contentType(ContentType.JSON)
                .pathParam("username", newUsername)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(UserDto::class.java)

        assertEquals(newUsername, foundUser.username)
        assertEquals(userDto1.password, foundUser.password)
        assertEquals(userDto1.health, foundUser.health)
        assertEquals(userDto1.equipment, foundUser.equipment)
    }

    @Test
    fun setUsernameInvalid() {
        val userDto1 = getValidUserDtos()[0]
        val postedId = postUserDto(userDto1, 201)
        val tooLongUsername = getTooLongUsername()


        // Too long username
        given().pathParam("id", postedId)
                .body(tooLongUsername)
                .patch("/{id}")
                .then()
                .statusCode(400)

        // Empty body
        given().pathParam("id", postedId)
                .body(" ")
                .patch("/{id}")
                .then()
                .statusCode(400)

        given().pathParam("username", userDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto1.username))
                .body("password", equalTo(userDto1.password))
                .body("health", equalTo(userDto1.health))
                .body("experience", equalTo(userDto1.experience))

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
                        listOf(1L,2L,3L)
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
                        listOf(1L,3L ,2L)
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
                        listOf(1L,2L,3L)
                )
        )
    }

    private fun getTooLongUsername() =
            "somethingLongerThan50Characters_aoisdjasiojdaoisjdoaisdjisdijasdoiasdjaosidjaoisjdoaisjdaoisjdoiajsdiojasidojaosijdaoisjdoaisjdoaijsdiojasdiojasdoijaisodjaoisjdaoisjdoiasjdoiajsdoiajsdiojadoijdgapi nasdfasdioufhasdifasidfuhasdifhasodfihasduifhaisuodfhasidfh aohguidsfhuidhgsdfiuhsdiuofhgsdoifughsdioufhiusdfiusdfhgsidfhgsidofhgsdf"


}
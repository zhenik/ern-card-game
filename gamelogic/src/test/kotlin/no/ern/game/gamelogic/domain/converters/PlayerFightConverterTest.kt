//package no.ern.game.gamelogic.domain.converters
//
//import no.ern.game.schema.dto.ItemDto
//import no.ern.game.schema.dto.PlayerDto
//import no.ern.game.gamelogic.domain.model.Character
//
//import org.junit.Assert.*
//import org.junit.Test
//
//class PlayerFightConverterTest{
//
//    val items = listOf(
//            ItemDto("sword","long sword","good",10,0),
//            ItemDto("armor","metal heavy armor","excelent",0,5)
//    )
//    val userDto = PlayerDto(
//            "1",
//            "attackerName",
//            "some pass",
//            "some salt",
//            100,
//            10,
//            305,
//            1000,
//            1,
//            listOf(1,3))
//
//    @Test
//    fun testTransform(){
//
//        // Act
//        val character: Character = PlayerFightConverter.transform(userDto,items)
//
//        // Assert
//        assertEquals(20, character.damage)
//        assertEquals(105, character.health)
//    }
//
//}
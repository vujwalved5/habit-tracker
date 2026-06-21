package com.example.habittracker.presentation.reminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.UUID

class UuidCollisionTest {

    @Test
    fun testUuidBitMixingCollisions() {
        val count = 10000
        val uuids = List(count) { UUID.randomUUID() }
        val generatedCodes = mutableSetOf<Int>()

        for (uuid in uuids) {
            val code = (uuid.mostSignificantBits xor uuid.leastSignificantBits).toInt()
            
            // Check that the same UUID always generates the same code
            val codeAgain = (uuid.mostSignificantBits xor uuid.leastSignificantBits).toInt()
            assertEquals(code, codeAgain)
            
            // Note: Since Int is 32-bit and UUID is 128-bit, some collisions could occur theoretically
            // for extremely large sets, but for 10,000 random UUIDs, it should be highly unlikely.
            assertFalse("Collision detected for UUID $uuid", generatedCodes.contains(code))
            generatedCodes.add(code)
        }
    }
}

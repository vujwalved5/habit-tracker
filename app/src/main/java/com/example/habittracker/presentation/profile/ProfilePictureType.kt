package com.example.habittracker.presentation.profile

sealed class ProfilePictureType(val type: String) {
    object Color : ProfilePictureType("Color")
    object Gradient : ProfilePictureType("Gradient")
    object Image : ProfilePictureType("Image")

    companion object {
        fun fromString(type: String): ProfilePictureType {
            return when (type) {
                "Gradient" -> Gradient
                "Image" -> Image
                else -> Color
            }
        }
    }
}

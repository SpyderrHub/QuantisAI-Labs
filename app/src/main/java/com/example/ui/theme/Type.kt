package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSans = try {
    FontFamily(
        Font(R.font.plus_jakarta_sans_extra_light, FontWeight.ExtraLight),
        Font(R.font.plus_jakarta_sans_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_light, FontWeight.Light),
        Font(R.font.plus_jakarta_sans_light_italic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(R.font.plus_jakarta_sans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(R.font.plus_jakarta_sans_medium_italic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
        Font(R.font.plus_jakarta_sans_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
        Font(R.font.plus_jakarta_sans_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.plus_jakarta_sans_extra_bold, FontWeight.ExtraBold),
        Font(R.font.plus_jakarta_sans_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic)
    )
} catch (e: Throwable) {
    FontFamily.SansSerif
}

val Jersey10Charted = PlusJakartaSans
val RubikWetPaint = PlusJakartaSans

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    )
)

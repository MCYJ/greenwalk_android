package com.codehanzoom.greenwalk.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codehanzoom.greenwalk.R

val inter = FontFamily(
    Font(R.font.inter_black, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.inter_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.inter_light, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.inter_medium, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.inter_regular, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.inter_thin, FontWeight.Bold, FontStyle.Normal)
)

val zenDots = FontFamily(
    Font(R.font.zen_dots_regular, FontWeight.Normal, FontStyle.Normal)
)

val GW_Typography = Typography(
    labelLarge = TextStyle( // Large Btn 스타일
        fontFamily = inter,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
    ),
    labelSmall = TextStyle( // Small Btn 스타일
        fontFamily = inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),
    displayLarge = TextStyle( // 플로깅 기록 수치용 스타일
        fontFamily = zenDots,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
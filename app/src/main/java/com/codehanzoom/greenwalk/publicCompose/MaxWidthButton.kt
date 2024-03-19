package com.codehanzoom.greenwalk.publicCompose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codehanzoom.greenwalk.ui.theme.GreenWalkTheme


@Composable
fun MaxWidthButton(title: String ) {
    Button(
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff8CB369)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = { /*TODO*/ }) {
        Text(
            text = title,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MaxWidthButtonPreview() {
    GreenWalkTheme {
        MaxWidthButton("시작하기")
    }
}
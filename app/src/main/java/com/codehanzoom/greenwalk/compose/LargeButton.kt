package com.codehanzoom.greenwalk.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codehanzoom.greenwalk.ui.theme.GW_Typography
import com.codehanzoom.greenwalk.ui.theme.GreenWalkTheme
import com.codehanzoom.greenwalk.ui.theme.inter_bold

@Composable
fun LargeButton(title: String, modifier: Unit? = null, onClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff8CB369)
        ),
        modifier = Modifier.size(320.dp, 50.dp),
        onClick = onClick) {
        Text(
            text = title,
            style = GW_Typography.labelLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
@Preview(showBackground = true)
@Composable
fun LargeButtonPreview() {
    GreenWalkTheme {
        LargeButton(title = "플로깅 시작하기") {
        }
    }
}
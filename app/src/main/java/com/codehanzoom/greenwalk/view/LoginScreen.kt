package com.codehanzoom.greenwalk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.codehanzoom.greenwalk.R
import com.codehanzoom.greenwalk.compose.LargeButton
import com.codehanzoom.greenwalk.compose.MaxWidthButton
import com.codehanzoom.greenwalk.model.LoginRequestBody
import com.codehanzoom.greenwalk.ui.theme.GW_Green100
import com.codehanzoom.greenwalk.ui.theme.GW_Red200
import com.codehanzoom.greenwalk.ui.theme.GreenWalkTheme
import com.codehanzoom.greenwalk.viewModel.LoginViewModel

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmpty by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Box (
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.logo_greenwalk),
                contentDescription = null,
                modifier = Modifier.size(250.dp, 200.dp)
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
        LoginTextField(
            title = "이메일",
            value = email,
            onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(35.dp))
        LoginTextField(
            title = "비밀번호",
            value = password,
            onValueChange = { password = it }
        )
        Column(
            Modifier
                .fillMaxWidth()
                .height(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEmpty){
                Text(
                    text = errorMessage,
                    color = GW_Red200,
                )
            }
        }
    }
    LoginButton(
        navController = navController,
        email = email,
        password = password,
        onErrorMessageChange = { errorMessage = it; isEmpty = it.isNotEmpty() }
    )
}

@Composable
fun LoginTextField(title: String, value: String, onValueChange: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(horizontal = 20.dp),
        text = title,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp,
        color = Color(0xFF252525)
    )
    TextField(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions =
            when(title) {
                "이메일" -> KeyboardOptions(keyboardType = KeyboardType.Email)
                else -> { KeyboardOptions(keyboardType = KeyboardType.Password) }
            },
        visualTransformation =
            when(title) {
                "이메일" -> VisualTransformation.None
                else -> { PasswordVisualTransformation() }
            },
        placeholder = {
            when(title) {
                "이메일" -> Text(text = "이메일을 입력해 주세요")
                else -> { Text(text = "비밀번호를 입력해 주세요") }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedPlaceholderColor = Color.Black,
            focusedPlaceholderColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            focusedIndicatorColor = GW_Green100,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Composable
fun BottomContainer(navController: NavHostController) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        Text(
            text = "아이디 찾기",
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
        Divider(
            color = Color(0xFF252525),
            modifier = Modifier
                .fillMaxHeight()  //fill the max height
                .width(1.dp)
        )
        Text(
            text = "비밀번호 찾기",
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
        Divider(
            color = Color(0xFF252525),
            modifier = Modifier
                .fillMaxHeight()  //fill the max height
                .width(1.dp)
        )
        Text(
            text = "회원가입",
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            modifier = Modifier.clickable {
                navController.navigate("SignUpScreen")
            }
        )
    }
}

@Composable
fun LoginButton(
    navController: NavHostController,
    email: String,
    password: String,
    onErrorMessageChange: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 25.dp)
    ) {
        Column {
            MaxWidthButton(title = "로그인") {
                if (email.isEmpty() || password.isEmpty()) {
                    onErrorMessageChange("이메일 또는 비밀번호를 다시 확인해 주세요.")
                } else {
                    onErrorMessageChange("")
                    val userData = LoginRequestBody(email, password)
                    LoginViewModel(userData).retrofitWork(navController = navController)
                }
            }
        Spacer(modifier = Modifier.height(15.dp))
            BottomContainer(navController = navController)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginActivityPreview() {
    GreenWalkTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}
package com.example.fooddelivery.ui.screens.auth

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.fooddelivery.R
import com.example.fooddelivery.ui.theme.Orange

@Preview(showBackground = true)
@Composable
fun AuthScreen() {
    val imageSize= remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush=Brush.verticalGradient(
        listOf(
            Color.Transparent,Color.Black
        ),
        startY = imageSize.value.height.toFloat()/2,
    )
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
    ){
        Image(painter = painterResource(R.drawable.auth_background), contentDescription = null,
            modifier = Modifier.
                fillMaxSize()
                .matchParentSize()
                .alpha(0.8f)
                .onGloballyPositioned {
                    imageSize.value=it.size
                },
            contentScale = ContentScale.FillBounds
                )
        Box(
            modifier = Modifier.matchParentSize()
                .background(brush),
        ){
            Image(painter = painterResource(R.drawable.text_welcome), contentDescription = null
                , alignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = (imageSize.value.height/10).dp)
            )
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.padding(8.dp)
                        .align(Alignment.TopEnd)
        ) {
                Text(text = stringResource(R.string.skip), color = Orange)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(align = Alignment.Bottom)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.Gray))
            Text(text = stringResource(R.string.sign_in_with), color = Color.White, modifier = Modifier.padding(horizontal = 20.dp))
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.Gray))
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                , modifier = Modifier.padding(bottom = 8.dp)
                        .weight(1f)) {
                    Row(modifier = Modifier.wrapContentWidth()) {
                        Image(painter = painterResource(R.drawable.ic_facebook), contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp, top = 2.dp, bottom = 2.dp))
                        Text(text = stringResource(R.string.sign_in_with_facebook), color = Color.Black,
                            modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.wrapContentWidth()) {
                    Image(painter = painterResource(R.drawable.ic_google), contentDescription = null,
                        modifier = Modifier.padding(end = 10.dp, top = 2.dp, bottom = 2.dp))
                    Text(text = stringResource(R.string.sign_in_with_google), color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }
        Button(onClick = {},
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth().padding(10.dp)
                .border(1.dp, Color.White, RoundedCornerShape(32.dp))
        ) {
            Text(text = stringResource(R.string.sign_in_with_email_or_phone), color = Color.White)
        }
        Text(text = stringResource(R.string.already_have_an_account), color = Color.White)
    }

}




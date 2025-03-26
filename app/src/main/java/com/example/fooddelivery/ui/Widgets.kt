package com.example.fooddelivery.ui

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fooddelivery.R
import com.example.fooddelivery.ui.screens.auth.BaseAuthProviderViewModel
import com.example.fooddelivery.ui.theme.Orange
import com.example.fooddelivery.ui.theme.poppinsFontFamily

@Composable
fun SocialButtons(
    icon:Int,
    text:Int,
    onClick:()->Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        , modifier = modifier
            .padding(bottom = 8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(32.dp))) {
        Row(modifier = Modifier.wrapContentWidth()) {
            Image(painter = painterResource(icon), contentDescription = null,
                modifier = Modifier.padding(end = 10.dp, top = 2.dp, bottom = 2.dp))
            Text(text = stringResource(text), color = Color.Black,
                modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GroupSocialButtons(
    text: Int=R.string.sign_in_with,
    color:Color=Color.Gray,
    viewModel: BaseAuthProviderViewModel
){
    Column() {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier=Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),color=color)
            Text(text = stringResource(text), color = Color.Gray, modifier = Modifier.padding(horizontal = 20.dp))
            HorizontalDivider(modifier=Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),color=color)
        }
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        val context= LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialButtons(icon = R.drawable.ic_facebook,text = R.string.sign_in_with_facebook,onClick ={
                viewModel.onFacebookSignInClick(context as ComponentActivity)
            }, modifier = Modifier
                .weight(1f))
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            SocialButtons(icon = R.drawable.ic_google,text = R.string.sign_in_with_google,onClick = {
                viewModel.onGoogleSignInClick(context)
            }, modifier = Modifier
                .weight(1f))
        }
    }
}

@Composable
fun FoodHubTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedIndicatorColor= Orange,
        unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.4f)
    )
){
    Column(
        modifier=Modifier.padding(horizontal = 20.dp)
    ) {
        label?.let {
            Row() {
                it()
                Spacer(modifier=Modifier.padding(bottom = 30.dp))
            }
        }
        //Spacer(modifier=Modifier.padding(bottom=10.dp))
        OutlinedTextField(
                value=value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily
                ),
                label = null,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors
        )
    }
}
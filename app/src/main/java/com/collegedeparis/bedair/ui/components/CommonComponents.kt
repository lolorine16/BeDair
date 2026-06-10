package com.collegedeparis.bedair.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.collegedeparis.bedair.R
import com.collegedeparis.bedair.ui.theme.PrimaryGreen
import com.collegedeparis.bedair.ui.theme.SecondaryGrey

@Composable
fun BeDairLogo(modifier: Modifier = Modifier, logoSize: Int = 120, showText: Boolean = true) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.genoui),
            contentDescription = "BeDair Logo",
            modifier = Modifier.size(logoSize.dp)
        )
        if (showText) {
            Text(
                text = "BeDair",
                style = MaterialTheme.typography.displayLarge,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = (logoSize / 4).sp
            )
        }
    }
}

@Composable
fun BeDairButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SecondaryGrey,
    contentColor: Color = Color.Gray
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 18.sp
        )
    }
}

@Composable
fun BeDairTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = PrimaryGreen,
            focusedBorderColor = PrimaryGreen,
            cursorColor = PrimaryGreen
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        )
    )
}

@Composable
fun RoleOption(
    role: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(110.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) PrimaryGreen else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = role,
            tint = if (isSelected) PrimaryGreen else Color.LightGray,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = role,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) PrimaryGreen else Color.LightGray,
            fontSize = 14.sp
        )
    }
}

package com.wefood.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wefood.app.R

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit, onExplore: () -> Unit, onRestaurant: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Background)) {
        Box(Modifier.size(240.dp).align(Alignment.TopEnd).padding(top = 24.dp).clip(CircleShape).background(PanelLight))
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(20.dp))
            Brand(large = true)
            Spacer(Modifier.height(8.dp))
            Text("O sabor que você ama,\nperto de você.", textAlign = TextAlign.Center, color = Muted, fontSize = 17.sp, lineHeight = 23.sp)
            Spacer(Modifier.height(30.dp))
            Box(Modifier.fillMaxWidth().height(248.dp).clip(RoundedCornerShape(32.dp)).background(Panel), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.food_combo),
                    contentDescription = "Combo de hambúrguer",
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.height(30.dp))
            Text("Sua próxima refeição começa aqui", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Hint("Descubra restaurantes, escolha seus favoritos e acompanhe cada pedido.")
            Spacer(Modifier.height(32.dp))
            PrimaryButton("Entrar", onLogin)
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Criar conta", onRegister)
            Spacer(Modifier.height(16.dp))
            Text("Explorar o cardápio", color = Yellow, modifier = Modifier.clickable(onClick = onExplore), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(18.dp))
            Text("Tem um restaurante? Cadastre seu negócio", color = Muted, modifier = Modifier.clickable(onClick = onRestaurant), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FormField(label: String, icon: ImageVector, value: String, onValueChange: (String) -> Unit, password: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun LoginScreen(onBack: () -> Unit, onRegister: () -> Unit, onForgot: () -> Unit, onEnter: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Page("Entrar", onBack) {
        Spacer(Modifier.height(20.dp))
        Brand(large = true)
        Spacer(Modifier.height(12.dp))
        Text("Boas-vindas de volta!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Hint("Entre para continuar sua experiência no WeFood.")
        Spacer(Modifier.height(28.dp))
        FormField("E-mail", Icons.Default.Email, email, { email = it })
        Spacer(Modifier.height(12.dp))
        FormField("Senha", Icons.Default.Lock, password, { password = it }, password = true)
        Spacer(Modifier.height(12.dp))
        Text("Esqueci minha senha", color = Yellow, modifier = Modifier.align(Alignment.End).clickable(onClick = onForgot), fontSize = 13.sp)
        Spacer(Modifier.height(30.dp))
        PrimaryButton("Entrar", onEnter)
        Spacer(Modifier.height(22.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Não tem conta? ", color = Muted)
            Text("Cadastre-se", color = Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onRegister))
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onLogin: () -> Unit, onEnter: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Page("Criar conta", onBack) {
        Spacer(Modifier.height(10.dp))
        Text("Junte-se ao WeFood", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Hint("Preencha seus dados para descobrir os melhores sabores da sua região.")
        Spacer(Modifier.height(24.dp))
        FormField("Nome completo", Icons.Default.Person, name, { name = it })
        Spacer(Modifier.height(12.dp))
        FormField("E-mail", Icons.Default.Email, email, { email = it })
        Spacer(Modifier.height(12.dp))
        FormField("Celular", Icons.Default.Person, phone, { phone = it })
        Spacer(Modifier.height(12.dp))
        FormField("Senha", Icons.Default.Lock, password, { password = it }, password = true)
        Spacer(Modifier.height(10.dp))
        Hint("Ao continuar, você concorda com os Termos de Uso e a Política de Privacidade.")
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Criar conta", onEnter)
        Spacer(Modifier.height(22.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Já tem uma conta? ", color = Muted)
            Text("Entrar", color = Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onLogin))
        }
    }
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, onSent: () -> Unit) {
    var email by remember { mutableStateOf("") }
    Page("Recuperar senha", onBack) {
        Spacer(Modifier.height(36.dp))
        Box(Modifier.size(72.dp).clip(CircleShape).background(PanelLight), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Yellow, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text("Esqueceu sua senha?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Hint("Digite seu e-mail para receber as instruções de recuperação.")
        Spacer(Modifier.height(24.dp))
        FormField("E-mail", Icons.Default.Email, email, { email = it })
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Continuar", onSent)
    }
}

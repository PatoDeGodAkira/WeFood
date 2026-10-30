package com.wefood.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onTab: (Screen) -> Unit,
    onEdit: () -> Unit,
    onOrders: () -> Unit,
    onAddresses: () -> Unit,
    onPayment: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    onHelp: () -> Unit,
    onExit: () -> Unit,
    onRestaurant: () -> Unit
) {
    Page("Meu perfil", activeTab = Screen.Profile, onTab = onTab) {
        SurfaceCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(58.dp).clip(CircleShape).background(PanelLight), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Yellow, modifier = Modifier.size(35.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Olá, Alex!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Hint("alex@email.com")
                }
            }
        }
        SectionTitle("Minha conta")
        NavRow(Icons.Default.Edit, "Editar dados", "Nome, e-mail e telefone", onEdit)
        NavRow(Icons.AutoMirrored.Filled.ReceiptLong, "Meus pedidos", "Acompanhe e avalie", onOrders)
        NavRow(Icons.Default.LocationOn, "Meus endereços", "Locais de entrega", onAddresses)
        NavRow(Icons.Default.CreditCard, "Formas de pagamento", "Cartões e outras opções", onPayment)
        SectionTitle("Para parceiros")
        NavRow(Icons.Default.Person, "Área do restaurante", "Cadastre seu negócio e gerencie seu cardápio", onRestaurant)
        SectionTitle("Preferências")
        NavRow(Icons.Default.Notifications, "Notificações", "Novidades do WeFood", onNotifications)
        NavRow(Icons.Default.Settings, "Configurações", "Ajustes do aplicativo", onSettings)
        NavRow(Icons.AutoMirrored.Filled.Help, "Ajuda", "Dúvidas frequentes", onHelp)
        Spacer(Modifier.height(12.dp))
        NavRow(Icons.AutoMirrored.Filled.Logout, "Sair", null, onExit)
    }
}

@Composable
fun EditProfileScreen(onBack: () -> Unit, onSave: () -> Unit) {
    var name by remember { mutableStateOf("Alex Silva") }
    var email by remember { mutableStateOf("alex@email.com") }
    var phone by remember { mutableStateOf("(11) 99999-9999") }
    Page("Editar perfil", onBack) {
        Spacer(Modifier.height(14.dp))
        ProfileField("Nome completo", name) { name = it }
        ProfileField("E-mail", email) { email = it }
        ProfileField("Celular", phone) { phone = it }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Salvar alterações", onSave)
    }
}

@Composable
private fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(15.dp),
        singleLine = true
    )
}

@Composable
fun SettingsScreen(onBack: () -> Unit, onHelp: () -> Unit) {
    var pushEnabled by remember { mutableStateOf(true) }
    var promoEnabled by remember { mutableStateOf(false) }
    Page("Configurações", onBack) {
        SectionTitle("Notificações")
        SettingsSwitch("Atualizações de pedidos", "Acompanhe cada etapa da entrega", pushEnabled) { pushEnabled = it }
        SettingsSwitch("Ofertas e novidades", "Receba sugestões e promoções", promoEnabled) { promoEnabled = it }
        SectionTitle("Sobre")
        NavRow(Icons.AutoMirrored.Filled.Help, "Central de ajuda", "Perguntas frequentes", onHelp)
        Spacer(Modifier.height(15.dp))
        Hint("WeFood • Versão 1.0")
    }
}

@Composable
private fun SettingsSwitch(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Hint(subtitle)
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    var openQuestion by remember { mutableStateOf(-1) }
    val faqs = listOf(
        "Como acompanho meu pedido?" to "Abra Meus pedidos e selecione o pedido em andamento para ver o status e a previsão de entrega.",
        "Como altero meu endereço?" to "Em Meu perfil, abra Meus endereços para escolher ou adicionar um local de entrega.",
        "Quais formas de pagamento são aceitas?" to "Na finalização do pedido, escolha cartão, Pix ou dinheiro na entrega.",
        "Posso avaliar um pedido?" to "Após a entrega, abra Meus pedidos e toque em Avaliar pedido."
    )
    Page("Ajuda", onBack) {
        Text("Como podemos ajudar?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Hint("Encontre respostas rápidas para as dúvidas mais comuns.")
        SectionTitle("Perguntas frequentes")
        faqs.forEachIndexed { index, (question, answer) ->
            SurfaceCard(Modifier.fillMaxWidth(), onClick = { openQuestion = if (openQuestion == index) -1 else index }) {
                Text(question, fontWeight = FontWeight.Bold)
                if (openQuestion == index) {
                    Spacer(Modifier.height(8.dp))
                    Hint(answer)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        SectionTitle("Ainda precisa de ajuda?")
        SurfaceCard(Modifier.fillMaxWidth()) {
            Text("Central de atendimento", fontWeight = FontWeight.Bold)
            Hint("Encontre os canais de atendimento do WeFood aqui.")
        }
    }
}

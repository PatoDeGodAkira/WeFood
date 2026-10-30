package com.wefood.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wefood.app.R
import java.util.Locale

private fun currency(value: Double): String = "R$ ${String.format(Locale("pt", "BR"), "%.2f", value)}"

@Composable
fun CartScreen(onBack: () -> Unit, onCheckout: () -> Unit, onExplore: () -> Unit) {
    val quantities = remember { mutableStateListOf(1, 2, 1) }
    val cartFoods = foods.take(3)
    val unitPrices = listOf(25.0, 45.0, 15.0)
    val subtotal = quantities.indices.sumOf { quantities[it] * unitPrices[it] }
    Page("Meu carrinho", onBack) {
        Hint("Revise os itens antes de continuar.")
        Spacer(Modifier.height(18.dp))
        cartFoods.forEachIndexed { index, food ->
            if (quantities[index] > 0) {
                SurfaceCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FoodPhoto(food.image, Modifier.size(84.dp), crop = false)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(food.name, fontWeight = FontWeight.Bold)
                            Text(food.restaurant, color = Muted, fontSize = 12.sp)
                            Text(food.price, color = Yellow, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { if (quantities[index] > 0) quantities[index]-- }) {
                            Icon(Icons.Default.Remove, "Diminuir", tint = Yellow)
                        }
                        Text("${quantities[index]}", modifier = Modifier.padding(horizontal = 6.dp), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { quantities[index]++ }) { Icon(Icons.Default.Add, "Aumentar", tint = Yellow) }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
        if (subtotal == 0.0) {
            Spacer(Modifier.height(60.dp))
            Text("Seu carrinho está vazio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Hint("Explore o cardápio e encontre algo delicioso.")
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Explorar pratos", onExplore)
        } else {
            SectionTitle("Resumo")
            SummaryLine("Subtotal", currency(subtotal))
            SummaryLine("Taxa de entrega", "R$ 5,99")
            Spacer(Modifier.height(8.dp))
            SummaryLine("Total", currency(subtotal + 5.99), bold = true)
            Spacer(Modifier.height(22.dp))
            PrimaryButton("Avançar para pagamento", onCheckout)
        }
    }
}

@Composable
private fun SummaryLine(label: String, amount: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(label, color = if (bold) Color.White else Muted, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
        Text(amount, color = if (bold) Yellow else Color.White, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun CheckoutScreen(onBack: () -> Unit, onAddress: () -> Unit, onPayment: () -> Unit, onConfirm: () -> Unit) {
    Page("Finalizar pedido", onBack) {
        SectionTitle("Endereço de entrega")
        NavRow(Icons.Default.LocationOn, "Casa", "Rua das Flores, 123 • Centro", onAddress)
        SectionTitle("Forma de pagamento")
        NavRow(Icons.Default.CreditCard, "Cartão de crédito", "Final 1234", onPayment)
        SectionTitle("Seu pedido")
        SurfaceCard(Modifier.fillMaxWidth()) {
            SummaryLine("1× Combo da casa", "R$ 25,00")
            SummaryLine("2× Açaí grande", "R$ 90,00")
            SummaryLine("1× Taco especial", "R$ 15,00")
        }
        SectionTitle("Detalhes do pagamento")
        SummaryLine("Produtos", "R$ 130,00")
        SummaryLine("Entrega", "R$ 5,99")
        SummaryLine("Total", "R$ 135,99", bold = true)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Confirmar pedido", onConfirm)
    }
}

@Composable
fun AddressesScreen(onBack: () -> Unit, onAdd: () -> Unit, onChoose: () -> Unit) {
    Page("Meus endereços", onBack) {
        Hint("Escolha onde deseja receber o pedido.")
        Spacer(Modifier.height(18.dp))
        SurfaceCard(Modifier.fillMaxWidth(), onChoose) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Yellow)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Casa  •  Padrão", fontWeight = FontWeight.Bold)
                    Hint("Rua das Flores, 123, Centro")
                    Hint("São Paulo, SP • 01000-000")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Adicionar endereço", onAdd, icon = Icons.Default.Add)
    }
}

@Composable
fun AddressFormScreen(onBack: () -> Unit, onSave: () -> Unit) {
    var zip by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    Page("Novo endereço", onBack) {
        Hint("Adicione as informações para facilitar a entrega.")
        Spacer(Modifier.height(16.dp))
        AddressField("CEP", zip) { zip = it }
        AddressField("Rua", street) { street = it }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) { AddressField("Número", number) { number = it } }
            Box(Modifier.weight(1f)) { AddressField("Complemento", complement) { complement = it } }
        }
        AddressField("Bairro", district) { district = it }
        AddressField("Nome do endereço (ex.: Casa)", label) { label = it }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Salvar endereço", onSave)
    }
}

@Composable
private fun AddressField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        singleLine = true,
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun PaymentScreen(onBack: () -> Unit, onSave: () -> Unit) {
    var method by remember { mutableStateOf("Cartão de crédito") }
    Page("Pagamento", onBack) {
        Hint("Escolha como prefere pagar seu pedido.")
        Spacer(Modifier.height(20.dp))
        listOf("Cartão de crédito", "Cartão de débito", "Pix", "Dinheiro na entrega").forEach { option ->
            SurfaceCard(Modifier.fillMaxWidth(), onClick = { method = option }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Yellow)
                    Spacer(Modifier.width(12.dp))
                    Text(option, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(if (option == method) "●" else "○", color = Yellow, fontSize = 21.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(15.dp))
        PrimaryButton("Usar $method", onSave)
    }
}

@Composable
fun OrderConfirmedScreen(onTracking: () -> Unit, onHome: () -> Unit) {
    Page {
        Spacer(Modifier.height(70.dp))
        Box(Modifier.size(96.dp).clip(CircleShape).background(PanelLight).align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(58.dp))
        }
        Spacer(Modifier.height(26.dp))
        Text("Pedido confirmado!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(8.dp))
        Text("Agora é só relaxar. Estamos preparando algo delicioso para você.", color = Muted, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(30.dp))
        SurfaceCard(Modifier.fillMaxWidth()) {
            SummaryLine("Número do pedido", "#WF2048")
            SummaryLine("Previsão de entrega", "30–45 min")
            SummaryLine("PIN da entrega", "5C68R5T", bold = true)
        }
        Spacer(Modifier.height(28.dp))
        PrimaryButton("Acompanhar pedido", onTracking)
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Voltar ao início", onHome)
    }
}

@Composable
fun TrackingScreen(onBack: () -> Unit, onReview: () -> Unit) {
    Page("Acompanhar pedido", onBack) {
        Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(22.dp))) {
            Image(painterResource(R.drawable.map_tracking), contentDescription = "Mapa ilustrativo da entrega", modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Crop)
            Tag("Pedido #WF2048", selected = true, onClick = {})
        }
        Spacer(Modifier.height(18.dp))
        Text("Seu pedido está a caminho!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Hint("Previsão de chegada: 7 minutos")
        SectionTitle("Status do pedido")
        listOf(
            "✓  Pedido confirmado" to "O restaurante recebeu seu pedido.",
            "✓  Sendo preparado" to "Seu lanche está sendo preparado.",
            "●  Saiu para entrega" to "O entregador está a caminho.",
            "○  Entregue" to "Aproveite sua refeição!"
        ).forEach { (title, subtitle) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(if (title.startsWith("○")) Muted else Yellow))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Hint(subtitle)
                }
            }
        }
        SectionTitle("Na entrega")
        SurfaceCard(Modifier.fillMaxWidth()) {
            SummaryLine("PIN de confirmação", "5C68R5T", bold = true)
            Hint("Mostre este código ao entregador quando receber o pedido.")
        }
        Spacer(Modifier.height(18.dp))
        SecondaryButton("Avaliar pedido", onReview)
    }
}

@Composable
fun OrdersScreen(onTab: (Screen) -> Unit, onTracking: () -> Unit, onReview: () -> Unit) {
    Page("Meus pedidos", activeTab = Screen.Orders, onTab = onTab) {
        Hint("Acompanhe seus pedidos e relembre seus favoritos.")
        SectionTitle("Em andamento")
        SurfaceCard(Modifier.fillMaxWidth(), onTracking) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FoodPhoto(R.drawable.food_combo, Modifier.size(76.dp), crop = false)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Pedido #WF2048", fontWeight = FontWeight.Bold)
                    Text("Burger Station • 3 itens", color = Muted, fontSize = 12.sp)
                    Text("Saiu para entrega", color = Success, fontSize = 12.sp)
                }
            }
        }
        SectionTitle("Anteriores")
        listOf("#WF1982" to "Sabor Caseiro", "#WF1830" to "Açaí do Bairro").forEach { (number, restaurant) ->
            SurfaceCard(Modifier.fillMaxWidth(), onReview) {
                Text("Pedido $number", fontWeight = FontWeight.Bold)
                Hint("$restaurant • Entregue")
                Spacer(Modifier.height(8.dp))
                Text("Avaliar pedido  →", color = Yellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun ReviewScreen(onBack: () -> Unit, onPost: () -> Unit) {
    var stars by remember { mutableIntStateOf(5) }
    var review by remember { mutableStateOf("") }
    Page("Avaliar pedido", onBack) {
        FoodPhoto(R.drawable.food_burger, Modifier.fillMaxWidth().height(175.dp))
        Spacer(Modifier.height(18.dp))
        Text("Como foi sua experiência?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Hint("Sua avaliação ajuda outras pessoas a escolherem melhor.")
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { index ->
                Icon(
                    Icons.Default.Star,
                    contentDescription = "$index estrelas",
                    tint = if (index <= stars) Yellow else Muted,
                    modifier = Modifier.size(38.dp).clickable { stars = index }
                )
            }
        }
        SectionTitle("Conte um pouco mais")
        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            placeholder = { Text("O que você achou da comida e da entrega?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(18.dp))
        SecondaryButton("Adicionar fotos", {})
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Publicar avaliação", onPost)
    }
}

package com.wefood.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class MenuDish(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: String,
    val available: Boolean = true,
    val featured: Boolean = false
)

private data class DemoOrder(val id: Int, val customer: String, val items: String, val total: String, val status: String)

@Composable
fun RestaurantPortalScreen(onBack: () -> Unit) {
    var registered by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf("Visão geral") }
    var restaurantName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var cnpj by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Campinas") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deliveryFee by remember { mutableStateOf("5,00") }
    var prepTime by remember { mutableStateOf("35") }
    var open by remember { mutableStateOf(true) }
    var showDishDialog by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var dishName by remember { mutableStateOf("") }
    var dishDescription by remember { mutableStateOf("") }
    var dishCategory by remember { mutableStateOf("Pratos principais") }
    var dishPrice by remember { mutableStateOf("") }
    var dishFeatured by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var nextId by remember { mutableStateOf(4) }

    val dishes = remember {
        mutableStateListOf(
            MenuDish(1, "Prato executivo", "Arroz, feijão, proteína e salada", "Pratos principais", "24,90", true, true),
            MenuDish(2, "Suco natural", "Suco feito na hora, 400 ml", "Bebidas", "8,00"),
            MenuDish(3, "Pudim da casa", "Fatia de pudim de leite", "Sobremesas", "9,50")
        )
    }

    val orders = remember {
        mutableStateListOf(
            DemoOrder(1042, "Mariana R.", "1x Prato executivo, 1x Suco", "R$ 32,90", "Novo"),
            DemoOrder(1041, "Pedro L.", "2x Prato executivo", "R$ 49,80", "Preparando"),
            DemoOrder(1040, "Camila S.", "1x Pudim da casa", "R$ 9,50", "Pronto")
        )
    }

    Page("Portal do restaurante", onBack) {
        if (!registered) {
            Box(Modifier.fillMaxWidth().padding(top = 6.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(72.dp).background(PanelLight, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Yellow, modifier = Modifier.size(38.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Venda pelo WeFood", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Hint("Crie o perfil do seu estabelecimento. Depois, você poderá cadastrar pratos, organizar o cardápio e acompanhar pedidos.")
            Spacer(Modifier.height(18.dp))
            SectionTitle("Dados do estabelecimento")
            PortalField("Nome do restaurante *", restaurantName) { restaurantName = it }
            PortalField("E-mail comercial *", email) { email = it }
            PortalField("Telefone", phone) { phone = it }
            PortalField("CNPJ (opcional nesta demonstração)", cnpj) { cnpj = it }
            PortalField("Descrição", description, singleLine = false) { description = it }
            PortalField("Endereço", address) { address = it }
            PortalField("Cidade", city) { city = it }
            PortalField("Taxa de entrega (R$)", deliveryFee) { deliveryFee = it }
            PortalField("Tempo médio de preparo (min)", prepTime) { prepTime = it }

            if (error.isNotBlank()) {
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            PrimaryButton("Criar perfil do restaurante", onClick = {
                error = when {
                    restaurantName.isBlank() -> "Informe o nome do restaurante."
                    email.isBlank() || !email.contains("@") -> "Informe um e-mail comercial válido."
                    city.isBlank() -> "Informe a cidade do estabelecimento."
                    else -> ""
                }
                if (error.isBlank()) registered = true
            })

            Spacer(Modifier.height(10.dp))
            Hint("Demonstração local: o cadastro ainda não é enviado ao servidor nem valida documentos comerciais.")
        } else {
            SurfaceCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).background(PanelLight, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Yellow, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(restaurantName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Hint("$city • ${if (open) "Aberto para pedidos" else "Fechado"}")
                    }
                    Switch(checked = open, onCheckedChange = { open = it })
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Pedidos hoje", "12", Modifier.weight(1f))
                MetricCard("Faturamento*", "R$ 386", Modifier.weight(1f))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Itens no cardápio", dishes.size.toString(), Modifier.weight(1f))
                MetricCard("Avaliação*", "4,8 ★", Modifier.weight(1f))
            }

            Hint("* Números ilustrativos para demonstrar a interface.")
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Visão geral", "Cardápio", "Pedidos", "Loja").forEach { label ->
                    TextButton(onClick = { tab = label }) {
                        Text(label, color = if (tab == label) Yellow else Muted, fontWeight = if (tab == label) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            HorizontalDivider(color = PanelLight)

            when (tab) {
                "Cardápio" -> {
                    SectionTitle("Seu cardápio", "Adicionar prato") {
                        editingId = null
                        dishName = ""
                        dishDescription = ""
                        dishCategory = "Pratos principais"
                        dishPrice = ""
                        dishFeatured = false
                        showDishDialog = true
                    }

                    if (dishes.isEmpty()) Hint("Seu cardápio está vazio. Adicione o primeiro prato.")

                    dishes.forEach { dish ->
                        SurfaceCard(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(dish.name, fontWeight = FontWeight.Bold, color = Color.White)
                                    Hint("${dish.category} • R$ ${dish.price}")
                                    if (dish.description.isNotBlank()) Hint(dish.description)
                                    Text(
                                        if (dish.available) "Disponível" else "Indisponível",
                                        color = if (dish.available) Success else Muted,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                IconButton(onClick = {
                                    editingId = dish.id
                                    dishName = dish.name
                                    dishDescription = dish.description
                                    dishCategory = dish.category
                                    dishPrice = dish.price
                                    dishFeatured = dish.featured
                                    showDishDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar prato", tint = Yellow)
                                }
                                IconButton(onClick = { dishes.remove(dish) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir prato", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = dish.available,
                                    onCheckedChange = { checked ->
                                        val index = dishes.indexOfFirst { it.id == dish.id }
                                        if (index >= 0) dishes[index] = dish.copy(available = checked)
                                    }
                                )
                                Text("Disponível para venda", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                Spacer(Modifier.weight(1f))
                                if (dish.featured) Tag("Destaque", true)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    SurfaceCard(Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = Yellow)
                        Text("Categorias de exemplo", fontWeight = FontWeight.Bold, color = Color.White)
                        Hint("Pratos principais • Bebidas • Sobremesas. A criação e ordenação de categorias poderá ser ligada à API.")
                    }
                }
                "Pedidos" -> {
                    SectionTitle("Pedidos recebidos")
                    orders.forEach { order ->
                        SurfaceCard(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Pedido #${order.id}", fontWeight = FontWeight.Bold, color = Color.White)
                                    Hint(order.customer)
                                    Hint(order.items)
                                    Text(order.total, color = Yellow, fontWeight = FontWeight.Bold)
                                }
                                Tag(order.status, true)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val next = when (order.status) {
                                    "Novo" -> "Aceito"
                                    "Aceito" -> "Preparando"
                                    "Preparando" -> "Pronto"
                                    "Pronto" -> "Saiu para entrega"
                                    else -> "Entregue"
                                }
                                PrimaryButton(next, modifier = Modifier.weight(1f), onClick = {
                                    val index = orders.indexOfFirst { it.id == order.id }
                                    if (index >= 0) orders[index] = order.copy(status = next)
                                })
                                TextButton(onClick = {
                                    val index = orders.indexOfFirst { it.id == order.id }
                                    if (index >= 0) orders[index] = order.copy(status = "Cancelado")
                                }) {
                                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Hint("Pedidos e mudanças de status são exemplos locais, não pedidos reais de clientes.")
                }
                "Loja" -> {
                    SectionTitle("Informações da loja")
                    PortalField("Nome", restaurantName) { restaurantName = it }
                    PortalField("E-mail comercial", email) { email = it }
                    PortalField("Telefone", phone) { phone = it }
                    PortalField("Descrição", description, singleLine = false) { description = it }
                    PortalField("Endereço", address) { address = it }
                    PortalField("Cidade", city) { city = it }
                    PortalField("Taxa de entrega (R$)", deliveryFee) { deliveryFee = it }
                    PortalField("Tempo médio (min)", prepTime) { prepTime = it }

                    PrimaryButton("Salvar informações", onClick = { tab = "Visão geral" })
                    Spacer(Modifier.height(8.dp))
                    Hint("Alterações ainda ficam somente nesta sessão do aplicativo.")
                }
                else -> {
                    SectionTitle("Ações rápidas")
                    NavRow(Icons.Default.Add, "Adicionar prato", "Inclua um item novo no cardápio") {
                        tab = "Cardápio"
                        editingId = null
                        dishName = ""
                        dishDescription = ""
                        dishPrice = ""
                        showDishDialog = true
                    }
                    NavRow(Icons.Default.Inventory2, "Gerenciar cardápio", "Editar preços e disponibilidade") { tab = "Cardápio" }
                    NavRow(Icons.Default.LocalShipping, "Ver pedidos", "Aceite e atualize pedidos") { tab = "Pedidos" }
                    NavRow(Icons.Default.Storefront, "Editar loja", "Endereço, contato e entrega") { tab = "Loja" }
                }
            }
        }
    }

    if (showDishDialog) {
        AlertDialog(
            onDismissRequest = { showDishDialog = false },
            title = { Text(if (editingId == null) "Adicionar prato" else "Editar prato") },
            text = {
                Column {
                    OutlinedTextField(dishName, { dishName = it }, label = { Text("Nome do prato *") }, singleLine = true)
                    OutlinedTextField(dishDescription, { dishDescription = it }, label = { Text("Descrição") })
                    OutlinedTextField(dishCategory, { dishCategory = it }, label = { Text("Categoria") }, singleLine = true)
                    OutlinedTextField(dishPrice, { dishPrice = it }, label = { Text("Preço (ex.: 24,90) *") }, singleLine = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dishFeatured, onCheckedChange = { dishFeatured = it })
                        Text("Destacar no cardápio")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val normalizedPrice = dishPrice.trim().replace(',', '.')
                    val priceValue = normalizedPrice.toDoubleOrNull()
                    if (dishName.isNotBlank() && priceValue != null && priceValue > 0) {
                        val cleanPrice = String.format(java.util.Locale("pt", "BR"), "%.2f", priceValue).replace('.', ',')
                        val existing = editingId
                        if (existing == null) {
                            dishes.add(MenuDish(nextId++, dishName.trim(), dishDescription.trim(), dishCategory.ifBlank { "Outros" }, cleanPrice, true, dishFeatured))
                        } else {
                            val index = dishes.indexOfFirst { it.id == existing }
                            if (index >= 0) {
                                dishes[index] = dishes[index].copy(
                                    name = dishName.trim(),
                                    description = dishDescription.trim(),
                                    category = dishCategory.ifBlank { "Outros" },
                                    price = cleanPrice,
                                    featured = dishFeatured
                                )
                            }
                        }
                        showDishDialog = false
                    }
                }) { Text("Salvar") }
            },
            dismissButton = { TextButton(onClick = { showDishDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun PortalField(label: String, value: String, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3
    )
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    SurfaceCard(modifier) {
        Text(label, color = Muted, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleLarge, color = Yellow, fontWeight = FontWeight.Bold)
    }
}
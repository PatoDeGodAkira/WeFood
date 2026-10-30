package com.wefood.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wefood.app.R

private val categories = listOf("Todos", "Hambúrguer", "Brasileira", "Açaí", "Mexicana")

@Composable
fun HomeScreen(
    onTab: (Screen) -> Unit,
    onSearch: () -> Unit,
    onCategories: () -> Unit,
    onOffers: () -> Unit,
    onAddress: () -> Unit,
    onRestaurant: (RestaurantItem) -> Unit,
    onFood: (FoodItem) -> Unit,
    onNotifications: () -> Unit,
    onCart: () -> Unit
) {
    Page(activeTab = Screen.Home, onTab = onTab) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Brand(Modifier.weight(1f))
            IconButton(onClick = onNotifications) { Icon(Icons.Default.Notifications, "Notificações", tint = Yellow) }
            IconButton(onClick = onCart) { Icon(Icons.Default.ShoppingCart, "Carrinho", tint = Yellow) }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Yellow, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(5.dp))
            Text("Entregar em", color = Muted, fontSize = 12.sp)
        }
        Text("Rua das Flores, 123  ⌄", fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onAddress))
        Spacer(Modifier.height(22.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).clickable(onClick = onSearch).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Muted)
                Spacer(Modifier.width(10.dp))
                Text("O que você quer comer hoje?", color = Muted)
            }
        }
        SectionTitle("Categorias", "Ver todas", onCategories)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            listOf("🍔 Hambúrguer", "🍽 Brasileira", "🥣 Açaí", "🌮 Mexicana", "🍟 Combos").forEach {
                Tag(it, onClick = onCategories)
            }
        }
        Spacer(Modifier.height(24.dp))
        SurfaceCard(Modifier.fillMaxWidth(), onOffers) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("OFERTA DA SEMANA", color = Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Seu favorito com\num preço especial", fontSize = 21.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("Confira as ofertas  →", color = Yellow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                FoodPhoto(R.drawable.food_burger, Modifier.size(108.dp))
            }
        }
        SectionTitle("Restaurantes próximos", "Ver todos", onSearch)
        restaurants.take(3).forEach { restaurant ->
            RestaurantCard(restaurant) { onRestaurant(restaurant) }
            Spacer(Modifier.height(10.dp))
        }
        SectionTitle("Mais pedidos", "Ver todos", onSearch)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            foods.take(4).forEach { item -> FoodCard(item, Modifier.width(185.dp)) { onFood(item) } }
        }
    }
}

@Composable
fun SearchScreen(onTab: (Screen) -> Unit, onMap: () -> Unit, onRestaurant: (RestaurantItem) -> Unit, onFood: (FoodItem) -> Unit) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todos") }
    val matchingFoods = foods.filter {
        (category == "Todos" || it.category == category) &&
            (query.isBlank() || it.name.contains(query, true) || it.restaurant.contains(query, true))
    }
    val matchingRestaurants = restaurants.filter { query.isBlank() || it.name.contains(query, true) || it.type.contains(query, true) }
    Page("Buscar", activeTab = Screen.Search, onTab = onTab) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Prato ou restaurante") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(15.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { item -> Tag(item, selected = category == item) { category = item } }
        }
        Spacer(Modifier.height(16.dp))
        SecondaryButton("Explorar no mapa", onMap)
        SectionTitle("Restaurantes")
        if (matchingRestaurants.isEmpty()) Hint("Nenhum restaurante encontrado.")
        matchingRestaurants.forEach { item ->
            RestaurantCard(item) { onRestaurant(item) }
            Spacer(Modifier.height(10.dp))
        }
        SectionTitle("Pratos")
        if (matchingFoods.isEmpty()) Hint("Nenhum prato encontrado.")
        matchingFoods.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { item -> FoodCard(item, Modifier.weight(1f)) { onFood(item) } }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun MapScreen(onBack: () -> Unit, onRestaurant: (RestaurantItem) -> Unit) {
    Page("Restaurantes no mapa", onBack) {
        Hint("Explore opções perto do seu endereço.")
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(390.dp).clip(RoundedCornerShape(20.dp))) {
            Image(
                painter = painterResource(R.drawable.map_discovery),
                contentDescription = "Mapa ilustrativo com restaurantes próximos",
                modifier = Modifier.fillMaxWidth().height(390.dp),
                contentScale = ContentScale.Crop
            )
        }
        SectionTitle("Perto de você")
        restaurants.take(3).forEach { item ->
            RestaurantCard(item) { onRestaurant(item) }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun CategoriesScreen(onBack: () -> Unit, onFood: (FoodItem) -> Unit) {
    var selected by remember { mutableStateOf("Todos") }
    Page("Categorias", onBack) {
        Hint("Encontre algo para cada momento do dia.")
        Spacer(Modifier.height(18.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category -> Tag(category, selected == category) { selected = category } }
        }
        SectionTitle(if (selected == "Todos") "Todos os pratos" else selected)
        foods.filter { selected == "Todos" || it.category == selected }.forEach { item ->
            SurfaceCard(Modifier.fillMaxWidth(), onClick = { onFood(item) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FoodPhoto(item.image, Modifier.size(82.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold)
                        Text(item.restaurant, color = Muted, fontSize = 12.sp)
                        Text(item.price, color = Yellow, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.Add, contentDescription = null, tint = Yellow)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun OffersScreen(onBack: () -> Unit, onFood: (FoodItem) -> Unit) {
    Page("Ofertas", onBack) {
        SurfaceCard(Modifier.fillMaxWidth()) {
            Text("SABORES EM DESTAQUE", color = Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Ofertas para aproveitar hoje", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Hint("Escolha seu próximo favorito com preços especiais.")
        }
        SectionTitle("Selecionados para você")
        listOf(foods[0], foods[3], foods[1]).forEach { item ->
            SurfaceCard(Modifier.fillMaxWidth(), onClick = { onFood(item) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FoodPhoto(item.image, Modifier.size(100.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Tag("Oferta")
                        Spacer(Modifier.height(6.dp))
                        Text(item.name, fontWeight = FontWeight.Bold)
                        Text(item.restaurant, color = Muted, fontSize = 12.sp)
                        Text(item.price, color = Yellow, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun RestaurantScreen(item: RestaurantItem, onBack: () -> Unit, onFood: (FoodItem) -> Unit, onCart: () -> Unit) {
    Page(item.name, onBack) {
        FoodPhoto(item.image, Modifier.fillMaxWidth().height(190.dp))
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(item.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Favorite, contentDescription = "Favoritar", tint = Yellow)
        }
        Text(item.type, color = Muted)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            Text("★ 4,8", color = Yellow, fontWeight = FontWeight.Bold)
            Text("◷ ${item.eta}", color = Muted)
            Text(item.fee, color = Success)
        }
        SectionTitle("Sobre o restaurante")
        Hint("Comida feita com ingredientes frescos e muito carinho. Peça seus favoritos e receba em casa.")
        SectionTitle("Destaques do cardápio")
        val menu = foods.filter { it.restaurant == item.name }.ifEmpty { foods.take(3) }
        menu.forEach { food ->
            SurfaceCard(Modifier.fillMaxWidth(), onClick = { onFood(food) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FoodPhoto(food.image, Modifier.size(92.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(food.name, fontWeight = FontWeight.Bold)
                        Text(food.description, color = Muted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(5.dp))
                        Text(food.price, color = Yellow, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.Add, contentDescription = null, tint = Yellow)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Ver carrinho", onCart, icon = Icons.Default.ShoppingCart)
    }
}

@Composable
fun ProductScreen(item: FoodItem, onBack: () -> Unit, onAdd: () -> Unit) {
    var quantity by remember { mutableIntStateOf(1) }
    var selected by remember { mutableStateOf("Padrão") }
    var note by remember { mutableStateOf("") }
    Page("Detalhes do prato", onBack) {
        FoodPhoto(item.image, Modifier.fillMaxWidth().height(235.dp), crop = item.image !in listOf(R.drawable.food_combo, R.drawable.food_taco))
        Spacer(Modifier.height(18.dp))
        Text(item.restaurant, color = Yellow, fontWeight = FontWeight.SemiBold)
        Text(item.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Hint(item.description)
        Spacer(Modifier.height(10.dp))
        Text(item.price, color = Yellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        SectionTitle("Escolha o tamanho")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Padrão", "Grande").forEach { option -> Tag(option, selected == option) { selected = option } }
        }
        SectionTitle("Alguma observação?")
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("Ex.: sem cebola, por favor") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Tag("−") { if (quantity > 1) quantity-- }
            Text("$quantity", Modifier.padding(horizontal = 24.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Tag("+") { quantity++ }
        }
        Spacer(Modifier.height(18.dp))
        PrimaryButton("Adicionar ao carrinho", onAdd, icon = Icons.Default.ShoppingCart)
    }
}

@Composable
fun FavoritesScreen(onTab: (Screen) -> Unit, onFood: (FoodItem) -> Unit, onRestaurant: (RestaurantItem) -> Unit) {
    Page("Favoritos", activeTab = Screen.Favorites, onTab = onTab) {
        Hint("Seus sabores preferidos, sempre por perto.")
        SectionTitle("Restaurantes")
        restaurants.take(2).forEach { item ->
            RestaurantCard(item) { onRestaurant(item) }
            Spacer(Modifier.height(10.dp))
        }
        SectionTitle("Pratos")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            foods.take(2).forEach { item -> FoodCard(item, Modifier.weight(1f)) { onFood(item) } }
        }
    }
}

@Composable
fun NotificationsScreen(onBack: () -> Unit, onTracking: () -> Unit, onOffers: () -> Unit) {
    Page("Notificações", onBack) {
        Hint("Novidades e atualizações do seu pedido.")
        Spacer(Modifier.height(18.dp))
        SurfaceCard(Modifier.fillMaxWidth(), onTracking) {
            Text("Pedido a caminho 🚴", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Hint("Seu pedido #WF2048 saiu para entrega. Acompanhe o trajeto.")
            Text("Agora há pouco", color = Yellow, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(10.dp))
        SurfaceCard(Modifier.fillMaxWidth(), onOffers) {
            Text("Tem oferta esperando por você", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Hint("Selecionamos pratos especiais para sua próxima refeição.")
            Text("Hoje", color = Yellow, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

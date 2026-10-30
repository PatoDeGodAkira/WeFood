package com.wefood.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Brand(modifier: Modifier = Modifier, large: Boolean = false) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White)) { append("We") }
            withStyle(SpanStyle(color = Yellow)) { append("Food") }
        },
        modifier = modifier,
        fontSize = if (large) 42.sp else 25.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-1).sp
    )
}

@Composable
fun Page(
    title: String = "",
    onBack: (() -> Unit)? = null,
    activeTab: Screen? = null,
    onTab: (Screen) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (activeTab != null) {
                AppBottomBar(activeTab, onTab)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            if (title.isNotBlank()) {
                PageHeader(title, onBack)
            } else {
                Spacer(Modifier.height(18.dp))
            }
            content()
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun PageHeader(title: String, onBack: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun AppBottomBar(active: Screen, onTab: (Screen) -> Unit) {
    val tabs = listOf(
        Triple(Screen.Home, "Início", Icons.Default.Home),
        Triple(Screen.Search, "Buscar", Icons.Default.Search),
        Triple(Screen.Orders, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
        Triple(Screen.Favorites, "Favoritos", Icons.Default.Favorite),
        Triple(Screen.Profile, "Perfil", Icons.Default.Person)
    )
    NavigationBar(containerColor = Panel, tonalElevation = 0.dp) {
        tabs.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = active == screen,
                onClick = { onTab(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Background,
                    selectedTextColor = Yellow,
                    indicatorColor = Yellow,
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = Background)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(15.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Yellow)
    ) {
        Text(text, color = Yellow, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SectionTitle(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        if (action != null) {
            Text(
                action,
                color = Yellow,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
fun SurfaceCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun FoodPhoto(image: Int, modifier: Modifier = Modifier, crop: Boolean = true) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Cream),
        contentScale = if (crop) ContentScale.Crop else ContentScale.Fit
    )
}

@Composable
fun FoodCard(item: FoodItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Column(Modifier.padding(10.dp)) {
            FoodPhoto(
                item.image,
                Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                crop = item.image !in listOf(com.wefood.app.R.drawable.food_combo, com.wefood.app.R.drawable.food_taco)
            )
            Spacer(Modifier.height(10.dp))
            Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
            Text(item.restaurant, color = Muted, fontSize = 12.sp, maxLines = 1)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.price, color = Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Star, contentDescription = null, tint = Yellow, modifier = Modifier.size(15.dp))
                Text(item.rating, fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun RestaurantCard(item: RestaurantItem, onClick: () -> Unit) {
    SurfaceCard(Modifier.fillMaxWidth(), onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FoodPhoto(item.image, Modifier.size(90.dp), crop = true)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(item.type, color = Muted, fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.height(10.dp))
                Text("★ 4,8  •  ${item.eta}", color = Yellow, fontSize = 12.sp)
                Text(item.fee, color = Muted, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
        }
    }
}

@Composable
fun NavRow(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PanelLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Yellow, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Color.White)
            if (subtitle != null) Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
    }
}

@Composable
fun Hint(text: String) {
    Text(text, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
}

@Composable
fun Tag(text: String, selected: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Yellow else PanelLight)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(text, color = if (selected) Background else Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
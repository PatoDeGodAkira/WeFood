package com.wefood.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WeFoodApp() {
    var current by rememberSaveable { mutableStateOf(Screen.Welcome) }
    val history = remember { mutableStateListOf<Screen>() }
    var selectedFood by remember { mutableStateOf(foods.first()) }
    var selectedRestaurant by remember { mutableStateOf(restaurants.first()) }

    fun navigate(screen: Screen) {
        if (screen != current) {
            history.add(current)
            current = screen
        }
    }

    fun back() {
        current = if (history.isNotEmpty()) history.removeAt(history.lastIndex) else Screen.Home
    }

    fun tab(screen: Screen) {
        history.clear()
        current = screen
    }

    fun openFood(item: FoodItem) {
        selectedFood = item
        navigate(Screen.Product)
    }

    fun openRestaurant(item: RestaurantItem) {
        selectedRestaurant = item
        navigate(Screen.Restaurant)
    }

    BackHandler(enabled = current != Screen.Welcome) { back() }

    when (current) {
        Screen.Welcome -> WelcomeScreen(
            onLogin = { navigate(Screen.Login) },
            onRegister = { navigate(Screen.Register) },
            onExplore = { tab(Screen.Home) },
            onRestaurant = { navigate(Screen.RestaurantPortal) }
        )
        Screen.Login -> LoginScreen(
            onBack = ::back,
            onRegister = { navigate(Screen.Register) },
            onForgot = { navigate(Screen.ForgotPassword) },
            onEnter = { tab(Screen.Home) }
        )
        Screen.Register -> RegisterScreen(onBack = ::back, onLogin = { navigate(Screen.Login) }, onEnter = { tab(Screen.Home) })
        Screen.ForgotPassword -> ForgotPasswordScreen(onBack = ::back, onSent = { navigate(Screen.Login) })
        Screen.Home -> HomeScreen(
            onTab = ::tab,
            onSearch = { tab(Screen.Search) },
            onCategories = { navigate(Screen.Categories) },
            onOffers = { navigate(Screen.Offers) },
            onAddress = { navigate(Screen.Addresses) },
            onRestaurant = ::openRestaurant,
            onFood = ::openFood,
            onNotifications = { navigate(Screen.Notifications) },
            onCart = { navigate(Screen.Cart) }
        )
        Screen.Search -> SearchScreen(
            onTab = ::tab,
            onMap = { navigate(Screen.Map) },
            onRestaurant = ::openRestaurant,
            onFood = ::openFood
        )
        Screen.Map -> MapScreen(onBack = ::back, onRestaurant = ::openRestaurant)
        Screen.Categories -> CategoriesScreen(onBack = ::back, onFood = ::openFood)
        Screen.Offers -> OffersScreen(onBack = ::back, onFood = ::openFood)
        Screen.Restaurant -> RestaurantScreen(selectedRestaurant, onBack = ::back, onFood = ::openFood, onCart = { navigate(Screen.Cart) })
        Screen.Product -> ProductScreen(selectedFood, onBack = ::back, onAdd = { navigate(Screen.Cart) })
        Screen.Cart -> CartScreen(onBack = ::back, onCheckout = { navigate(Screen.Checkout) }, onExplore = { tab(Screen.Search) })
        Screen.Checkout -> CheckoutScreen(
            onBack = ::back,
            onAddress = { navigate(Screen.Addresses) },
            onPayment = { navigate(Screen.Payment) },
            onConfirm = { history.clear(); current = Screen.OrderConfirmed }
        )
        Screen.Addresses -> AddressesScreen(onBack = ::back, onAdd = { navigate(Screen.AddressForm) }, onChoose = ::back)
        Screen.AddressForm -> AddressFormScreen(onBack = ::back, onSave = ::back)
        Screen.Payment -> PaymentScreen(onBack = ::back, onSave = ::back)
        Screen.OrderConfirmed -> OrderConfirmedScreen(onTracking = { navigate(Screen.Tracking) }, onHome = { tab(Screen.Home) })
        Screen.Tracking -> TrackingScreen(onBack = ::back, onReview = { navigate(Screen.Review) })
        Screen.Orders -> OrdersScreen(onTab = ::tab, onTracking = { navigate(Screen.Tracking) }, onReview = { navigate(Screen.Review) })
        Screen.Favorites -> FavoritesScreen(onTab = ::tab, onFood = ::openFood, onRestaurant = ::openRestaurant)
        Screen.Review -> ReviewScreen(onBack = ::back, onPost = { tab(Screen.Orders) })
        Screen.Notifications -> NotificationsScreen(onBack = ::back, onTracking = { navigate(Screen.Tracking) }, onOffers = { navigate(Screen.Offers) })
        Screen.Profile -> ProfileScreen(
            onTab = ::tab,
            onEdit = { navigate(Screen.EditProfile) },
            onOrders = { tab(Screen.Orders) },
            onAddresses = { navigate(Screen.Addresses) },
            onPayment = { navigate(Screen.Payment) },
            onNotifications = { navigate(Screen.Notifications) },
            onSettings = { navigate(Screen.Settings) },
            onHelp = { navigate(Screen.Help) },
            onExit = { history.clear(); current = Screen.Welcome },
            onRestaurant = { navigate(Screen.RestaurantPortal) }
        )
        Screen.EditProfile -> EditProfileScreen(onBack = ::back, onSave = ::back)
        Screen.Settings -> SettingsScreen(onBack = ::back, onHelp = { navigate(Screen.Help) })
        Screen.Help -> HelpScreen(onBack = ::back)
        Screen.RestaurantPortal -> RestaurantPortalScreen(onBack = ::back)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomePreview() {
    WeFoodTheme {
        HomeScreen({}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LoginPreview() {
    WeFoodTheme {
        LoginScreen({}, {}, {}, {})
    }
}

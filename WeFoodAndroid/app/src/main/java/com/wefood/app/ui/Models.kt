package com.wefood.app.ui

import androidx.annotation.DrawableRes
import com.wefood.app.R

enum class Screen {
    Welcome, Login, Register, ForgotPassword,
    Home, Search, Map, Categories, Offers, Restaurant, Product,
    Cart, Checkout, Addresses, AddressForm, Payment,
    OrderConfirmed, Tracking, Orders, Favorites, Review,
    Notifications, Profile, EditProfile, Settings, Help, RestaurantPortal
}

data class FoodItem(
    val name: String,
    val description: String,
    val price: String,
    val restaurant: String,
    val category: String,
    @DrawableRes val image: Int,
    val rating: String = "4,8"
)

val foods = listOf(
    FoodItem("Combo da casa", "Burger, batata crocante e bebida", "R$ 25,00", "Burger Station", "Hambúrguer", R.drawable.food_combo),
    FoodItem("Açaí grande", "Açaí com frutas e granola", "R$ 45,00", "Açaí do Bairro", "Açaí", R.drawable.food_acai),
    FoodItem("Taco especial", "Carne, salada fresca e molho da casa", "R$ 15,00", "Taco House", "Mexicana", R.drawable.food_taco),
    FoodItem("Burger artesanal", "Pão macio, carne suculenta e queijo", "R$ 29,90", "Burger Station", "Hambúrguer", R.drawable.food_burger),
    FoodItem("Burger duplo", "Dois hambúrgueres e molho especial", "R$ 36,90", "Burger Station", "Hambúrguer", R.drawable.food_burger_close),
    FoodItem("Prato da casa", "Refeição preparada com muito carinho", "R$ 34,90", "Sabor Caseiro", "Brasileira", R.drawable.food_cutlet)
)

data class RestaurantItem(
    val name: String,
    val type: String,
    val eta: String,
    val fee: String,
    @DrawableRes val image: Int
)

val restaurants = listOf(
    RestaurantItem("Sabor Caseiro", "Brasileira • pratos feitos", "30–45 min", "R$ 5,99", R.drawable.food_cutlet),
    RestaurantItem("Burger Station", "Hambúrgueres • combos", "20–35 min", "Entrega grátis", R.drawable.food_burger),
    RestaurantItem("Açaí do Bairro", "Açaí • sobremesas", "25–40 min", "R$ 3,99", R.drawable.food_acai),
    RestaurantItem("Taco House", "Mexicana • lanches", "35–50 min", "R$ 4,99", R.drawable.food_taco)
)

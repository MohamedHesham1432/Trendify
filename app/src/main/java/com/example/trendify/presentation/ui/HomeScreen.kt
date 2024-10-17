package com.example.trendify.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.example.trendify.R
import com.example.trendify.data.model.Banner
import com.example.trendify.data.model.Product
import com.example.trendify.presentation.viewmodel.HomeViewModel
import org.w3c.dom.Text

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = hiltViewModel()
        val homeResponse = viewModel.HomeResponse.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopBar()
            },
            bottomBar = {
                BottomNavigationBar()
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search product") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // Banner Section
                PromotionalBanner()

                // Categories
                CategoryRow()

                // Popular Products Section
                Text(
                    text = "Popular Product",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                // LazyColumn for Products
                homeResponse.value?.data?.products?.let { products ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products.filter { product ->
                            product.name.contains(searchQuery, ignoreCase = true)
                        }) { product ->
                            HomeProductCard(products = product)
                        }
                    }
                } ?: run {
                    // Handle empty or loading state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No products available")
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val navigator = LocalNavigator.currentOrThrow
    CenterAlignedTopAppBar(

        title = { Text("Home Screen") },
        navigationIcon = {
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },

        actions = {

            IconButton(onClick = { navigator.push(CartScreen()) }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
            }
            IconButton(onClick = { /* Handle notification click */ }) {
                BadgedBox(
                    badge = {
                        Badge {
                            Text("3") // This represents the notification count
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFFA500)) // Orange color
    )
}

@Composable
fun PromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EA)) // Purple banner
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "A Spring Surprise",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Text(
                text = "Cashback 25%",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun CategoryRow() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(listOf("Flash Deal", "Bill", "Game", "Daily Gift", "More")) { category ->
            CategoryItem(category)
        }
    }
}

@Composable
fun CategoryItem(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .width(64.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.camera), contentDescription ="baseline_games " , modifier = Modifier.padding(16.dp) , colorFilter = ColorFilter.tint(Color(
            0xFFFF9800
        )
        ))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BottomNavigationBar() {
    val navigator = LocalNavigator.currentOrThrow
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { /* Handle home click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            selected = false,
            onClick = { /* Handle favorites click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { /* Handle profile click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            selected = false,
            onClick = { navigator.push(CartScreen()) }
        )
    }
}

@Composable
fun HomeProductCard(products: Product) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()){
            AsyncImage(model = products.image, contentDescription ="Image", modifier = Modifier.fillMaxWidth() , contentScale = ContentScale.FillWidth)

            Text("Welcome to Trenday",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 26.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))
        Column(Modifier.padding(start = 18.dp, end = 14.dp)) {

            Row( modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween ,verticalAlignment = Alignment.CenterVertically,  ) {
                Text(text = "Sale " ,
                    fontSize = 11.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,)

                Spacer(modifier = Modifier.weight(1f))
                Text(text = "View All ")
            }
            Spacer(modifier = Modifier.height(22.dp))
            //item
            Column {
                Box (modifier = Modifier.height(210.dp)){
                    AsyncImage( modifier = Modifier
                        .height(190.dp)
                        .width(150.dp), contentScale = ContentScale.Crop, model = products.image, contentDescription ="Image")
                    Button(onClick = {} , enabled = false, colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Red,containerColor = Color.Red) ,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .defaultMinSize(
                                minHeight = 10.dp, minWidth = 5.dp
                            ), contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp) ) {
                        Text(text = "-20 %" ,
                            color = Color.White ,
                            fontSize = 11.sp ,
                            textAlign = TextAlign.Center
                        )

                    }
                    Surface(color = Color.White  ,
                        shadowElevation = 10.dp,
                        shape = CircleShape  ,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = 5.dp)) {

                        IconButton(onClick = { } ) {
                            Icon(Icons.Outlined.Favorite, contentDescription = "Favorite" , tint = Color.Gray)

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                    var rating by remember { mutableStateOf(1f) } //default rating will be 1
                    Row(modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 7.dp) , verticalAlignment = Alignment.CenterVertically) {

                        StarRatingBar(
                            maxStars = 5,
                            rating = rating,
                            onRatingChanged = {
                                rating = it
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = products.name ,
                    fontSize = 20.sp ,
                    fontFamily = FontFamily.Cursive ,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Price: ${products.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = Color(0xFF4CAF50) // Green color for price
                )
                Text(
                    text = "Old Price: ${products.old_price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50) // Green color for price
                )

            }
        }


    }

}
@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val starSize = (8f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC107) else Color(0x20161515)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
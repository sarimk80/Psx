package com.example.psx

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.example.psx.ui.theme.PsxTheme
import com.example.psx.views.Home
import com.example.psx.views.HotStocks
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.psx.views.SectorView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MainApplication: Application()



enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
){
    Home("home","Home", Icons.Default.Home,"Home"),
    HotStocks("hot stocks","HotStocks",Icons.AutoMirrored.Filled.TrendingUp,"Hot stocks"),
    Sectors("sector","Sectors",Icons.Default.PieChart,"Sectors"),
    Portfolio("portfolio","Portfolio",Icons.Default.AccountBalanceWallet,"Portfolio"),
    Search("Search","Search",Icons.Default.Search,"Search")
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
){
    NavHost(
        navController,
        startDestination = startDestination.route
    ){
        Destination.entries.forEach { destination ->

        composable(destination.route){
            when(destination){
                Destination.Home -> Home()
                Destination.HotStocks -> HotStocks()
                Destination.Sectors -> SectorView()
                Destination.Portfolio -> HotStocks()
                Destination.Search -> Home()

            }
        }

        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val startDestination = Destination.Home
            val selectedDestination = rememberSaveable { mutableIntStateOf(0) }

            PsxTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                        Destination.entries.forEachIndexed { index, destination ->
                            NavigationBarItem(
                                selected = selectedDestination.intValue == index,
                                onClick = {
                                    navController.navigate(route = destination.route)
                                    selectedDestination.intValue = index
                                },
                                icon = {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.contentDescription
                                    )
                                },
                                label = {
                                    Text(destination.label)
                                }
                            )
                        }
                    }
            }

                    ) { innerPadding ->
                    AppNavHost(navController,startDestination, modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PsxTheme {
        Greeting("Android")
    }
}
package com.example.psx

import TickerDetailView
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.psx.ui.theme.PsxTheme
import com.example.psx.views.Home
import com.example.psx.views.HotStocks
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.psx.views.PortfolioView
import com.example.psx.views.SearchView
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
    Search("Search","Search",Icons.Default.Search,"Search");

    companion object {
        const val TICKER_DETAIL_ROUTE = "ticker_detail/{type}/{symbol}"

        fun getTickerDetailRoute(type: String, symbol: String): String {
            return "ticker_detail/$type/$symbol"
        }
    }
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
        composable(Destination.Home.route){
            Home()
        }
        composable(Destination.HotStocks.route){
            HotStocks(
                onTickerClick = {type,symbol ->
                    navController.navigate("ticker_detail/$type/$symbol")
                }
            )
        }
        composable(Destination.Sectors.route){
            SectorView()
        }
        composable(Destination.Portfolio.route){
            PortfolioView(
                onTickerClick = {type,symbol ->
                    navController.navigate("ticker_detail/$type/$symbol")
                }
            )
        }
        composable(Destination.Search.route){
            SearchView(
                onTickerClick = {type,symbol ->
                    navController.navigate("ticker_detail/$type/$symbol")
                }
            )
        }

        composable(
            route = "ticker_detail/{type}/{symbol}",
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = "REG" // Default market type
                },
                navArgument("symbol") {
                    type = NavType.StringType
                }
            )
        ){ backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "REG"
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""

            TickerDetailView(
                type = type,
                symbol = symbol,
                onBack = { navController.popBackStack() }
            )

        }


//        Destination.entries.forEach { destination ->
//
//        composable(destination.route){
//            when(destination){
//                Destination.Home -> Home()
//                Destination.HotStocks -> HotStocks()
//                Destination.Sectors -> SectorView()
//                Destination.Portfolio -> HotStocks()
//                Destination.Search -> Home()
//
//            }
//        }
//
//        }
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
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets,
                            //contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            ) {
                        Destination.entries.forEachIndexed { index, destination ->
                            NavigationBarItem(
                                alwaysShowLabel = false,
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
            },


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
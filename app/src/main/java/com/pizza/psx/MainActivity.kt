package com.pizza.psx

import com.pizza.psx.views.TickerDetailView
import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.pizza.psx.ui.theme.PsxTheme
import com.pizza.psx.views.Home
import com.pizza.psx.views.HotStocks
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pizza.psx.domain.model.DatumNavType
import com.pizza.psx.views.PortfolioView
import com.pizza.psx.views.SearchView
import com.pizza.psx.views.SectorDetailView
import com.pizza.psx.views.SectorView
import com.google.gson.Gson
import com.pizza.psx.views.IndexDetailView
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
    Home("home","Home", Icons.Filled.BarChart,"Home"),
    Portfolio("portfolio","Holdings",Icons.Default.AccountBalanceWallet,"Portfolio"),
    HotStocks("hot stocks","Movers",Icons.AutoMirrored.Filled.TrendingUp,"Hot stocks"),
    Sectors("sector","Industries",Icons.Default.PieChart,"Sectors"),
    Search("Search","Screener",Icons.Filled.FilterAlt,"Search");

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
            Home(
                onIndexClick = {indexSymbol ->
                    navController.navigate("indexDetail/$indexSymbol")
                }
            )
        }
        composable(Destination.HotStocks.route){
            HotStocks(
                onTickerClick = {type,symbol ->
                    navController.navigate("ticker_detail/$type/$symbol")
                }
            )
        }
        composable(Destination.Sectors.route){
            SectorView(
                onSectorClick = {sectorName,sector ->
                    val datatum = sector
                    val json = Uri.encode(Gson().toJson(datatum))
                    navController.navigate("sector_detail/$sectorName/$json")
                }
            )
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

        composable(
            route = "sector_detail/{sectorName}/{datum}",
            arguments = listOf(
                navArgument("sectorName") {
                    type = NavType.StringType
                },
                navArgument("datum") {
                    type = DatumNavType
                }
            )
        ) { backStackEntry ->
            val sectorName = backStackEntry.arguments?.getString("sectorName") ?: "REG"
            val datum = backStackEntry.arguments?.getString("datum")?.let {
                DatumNavType.parseValue(it)
            }
            SectorDetailView(
                sectorName = sectorName,
                sector = datum!!,
                onTickerClick = {type,symbol ->
                    navController.navigate("ticker_detail/$type/$symbol")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable  (
            route = "indexDetail/{indexSymbol}",
            arguments = listOf(
                navArgument("indexSymbol") {
                    type = NavType.StringType
                    defaultValue = "KSE100" // Default market type
                }

            )
        ){backStackEntry ->
            val indexSymbol = backStackEntry.arguments?.getString("indexSymbol") ?: "KSE100"
            IndexDetailView(
                indexSymbol =  indexSymbol,
                onTickerClick = {symbol ->
                    navController.navigate("ticker_detail/REG/$symbol")
                },
                onBackClick = { navController.popBackStack() }
            )
        }


    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val startDestination = Destination.Home
            val selectedDestination = rememberSaveable { mutableIntStateOf(0) }

            PsxTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets,
                            ) {
                        Destination.entries.forEachIndexed { index, destination ->
                            NavigationBarItem(
                                alwaysShowLabel = true,
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
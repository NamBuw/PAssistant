package com.avis.app.ptalk.navigation

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.avis.app.ptalk.core.auth.AuthentikAuthManager
import com.avis.app.ptalk.core.network.TokenManager
import com.avis.app.ptalk.ui.screen.auth.LoginScreen
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import com.avis.app.ptalk.ui.screen.config.DeviceDetailScreen
import com.avis.app.ptalk.ui.screen.config.HomeScreen
import com.avis.app.ptalk.ui.screen.config.ScanDeviceScreen

/**
 * Navigation for PTalk app including Auth and Config
 */
@Composable
fun ConfigAppNavGraph(
    navController: NavHostController,
    startDestination: String = Route.SPLASH,
    nextDestination: String = Route.LOGIN,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.SPLASH) {
            com.avis.app.ptalk.ui.screen.auth.SplashScreen(
                onSplashComplete = {
                    navController.navigate(nextDestination) {
                        popUpTo(Route.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.LOGIN) {
            val context = LocalContext.current
            val authManager = AuthentikAuthManager(context)

            val authLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val data = result.data ?: return@rememberLauncherForActivityResult
                val resp = AuthorizationResponse.fromIntent(data)
                val ex = AuthorizationException.fromIntent(data)

                if (ex != null || resp == null) return@rememberLauncherForActivityResult

                authManager.handleAuthorizationResponse(
                    data = data,
                    onSuccess = { authResult ->
                        TokenManager.init(context)
                        TokenManager.saveToken(
                            accessToken = authResult.accessToken,
                            refreshToken = authResult.refreshToken,
                            userId = authResult.userId
                        )
                        TokenManager.saveUserInfo(username = authResult.name, email = authResult.email, phone = null)
                        navController.navigate(Route.HOME) {
                            popUpTo(Route.LOGIN) { inclusive = true }
                        }
                    },
                    onError = { /* ignore */ }
                )
            }

            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    // Signup handled via Authentik - no in-app signup needed
                },
                onLaunchSSO = {
                    authLauncher.launch(authManager.getAuthorizationIntent())
                }
            )
        }

        // Home screen with PTIT logo
        composable(Route.HOME) {
            HomeScreen(
                onNavigateToScan = {
                    navController.navigate(Route.SCAN_DEVICE)
                },
                onNavigateToControl = { macAddress, deviceName ->
                    navController.navigate("${Route.CONTROL}/$macAddress/$deviceName")
                },
                onNavigateToDeviceDetail = { macAddress, deviceName, deviceId ->
                    val route = if (deviceId != null) {
                        "${Route.DEVICE_DETAIL}/$macAddress/$deviceName?deviceId=$deviceId"
                    } else {
                        "${Route.DEVICE_DETAIL}/$macAddress/$deviceName"
                    }
                    navController.navigate(route)
                },
                onSignOut = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Scan device screen with radar
        composable(Route.SCAN_DEVICE) {
            ScanDeviceScreen(
                onDeviceConnected = { deviceAddress ->
                    // Device connected, config dialog will show
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("${Route.CONTROL}/{macAddress}/{deviceName}") { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            val deviceName = backStackEntry.arguments?.getString("deviceName") ?: "PTalk Device"
            com.avis.app.ptalk.ui.screen.config.ControlScreen(
                macAddress = macAddress,
                deviceName = deviceName,
                onBack = { navController.popBackStack() }
            )
        }

        // Device detail with chat history
        composable("${Route.DEVICE_DETAIL}/{macAddress}/{deviceName}?deviceId={deviceId}") { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            val deviceName = backStackEntry.arguments?.getString("deviceName") ?: "PTalk Device"
            val deviceId = backStackEntry.arguments?.getString("deviceId")
            DeviceDetailScreen(
                macAddress = macAddress,
                deviceName = deviceName,
                deviceId = deviceId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

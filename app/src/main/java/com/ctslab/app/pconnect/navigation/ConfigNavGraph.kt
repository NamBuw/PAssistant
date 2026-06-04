package com.ctslab.app.pconnect.navigation

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ctslab.app.pconnect.core.auth.AuthentikAuthManager
import com.ctslab.app.pconnect.core.network.TokenManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.ctslab.app.pconnect.ui.screen.auth.LoginScreen
import com.ctslab.app.pconnect.ui.screen.auth.SignupScreen
import com.ctslab.app.pconnect.ui.viewmodel.auth.VMSignup
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import com.ctslab.app.pconnect.ui.screen.config.BannedWordsScreen
import com.ctslab.app.pconnect.ui.screen.config.DeviceDetailScreen
import com.ctslab.app.pconnect.ui.screen.config.HomeScreen
import com.ctslab.app.pconnect.ui.screen.config.ScanDeviceScreen
import com.ctslab.app.pconnect.ui.screen.config.SubscriptionScreen

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
            com.ctslab.app.pconnect.ui.screen.auth.SplashScreen(
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
                val data = result.data
                if (data == null) {
                    Toast.makeText(context, "Đăng nhập SSO không hoàn tất (bạn đã huỷ?)", Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }
                val resp = AuthorizationResponse.fromIntent(data)
                val ex = AuthorizationException.fromIntent(data)

                if (ex != null) {
                    // Lộ lỗi thật ra thay vì nuốt im lặng (huỷ, sai redirect, lỗi server…)
                    Log.w(
                        "ConfigNavGraph",
                        "SSO authorization failed: type=${ex.type} code=${ex.code} error=${ex.error} desc=${ex.errorDescription}",
                        ex
                    )
                    val msg = ex.errorDescription
                        ?: ex.error
                        ?: "Đăng nhập SSO không hoàn tất (bạn đã huỷ?)"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }
                if (resp == null) {
                    Toast.makeText(context, "Không nhận được phản hồi từ Authentik", Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }

                authManager.handleAuthorizationResponse(
                    data = data,
                    onSuccess = { authResult ->
                        val tokenManager = TokenManager(context)
                        tokenManager.saveToken(
                            accessToken = authResult.accessToken,
                            refreshToken = authResult.refreshToken,
                            userId = authResult.userId
                        )
                        val displayName = authResult.name.takeIf { !it.isNullOrBlank() }
                            ?: authResult.email?.substringBefore("@")
                            ?: "User"
                        tokenManager.saveUserInfo(
                            username = displayName,
                            email = authResult.email,
                            phone = null
                        )
                        navController.navigate(Route.HOME) {
                            popUpTo(Route.LOGIN) { inclusive = true }
                        }
                    },
                    onError = { errMsg ->
                        // Lỗi đổi code -> token (vd sai client_secret / PKCE) — hiện ra để chẩn đoán
                        Log.e("ConfigNavGraph", "Token exchange failed: $errMsg")
                        Toast.makeText(context, "Lỗi đổi token: $errMsg", Toast.LENGTH_LONG).show()
                    }
                )
            }

            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Route.SIGNUP)
                },
                onLaunchSSO = {
                    authLauncher.launch(authManager.getAuthorizationIntent())
                }
            )
        }

        composable(Route.SIGNUP) {
            val signupViewModel: VMSignup = hiltViewModel()
            val uiState by signupViewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onRegister = { username, email, password, confirmPassword ->
                    signupViewModel.register(username, email, password, confirmPassword)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onClearError = { signupViewModel.clearError() }
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
                onNavigateToBannedWords = {
                    navController.navigate(Route.BAN_KEYWORD)
                },
                onNavigateToSubscription = {
                    navController.navigate(Route.SUBSCRIPTION)
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
            com.ctslab.app.pconnect.ui.screen.config.ControlScreen(
                macAddress = macAddress,
                deviceName = deviceName,
                onBack = { navController.popBackStack() }
            )
        }

        // Banned words & topics management (parental moderation)
        composable(Route.BAN_KEYWORD) {
            BannedWordsScreen(onBack = { navController.popBackStack() })
        }

        // Subscription plans (Gói đăng ký)
        composable(Route.SUBSCRIPTION) {
            SubscriptionScreen(onBack = { navController.popBackStack() })
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

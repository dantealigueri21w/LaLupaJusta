package pe.appmobile.lalupajusta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository
import pe.appmobile.lalupajusta.ui.screens.CasoScreen
import pe.appmobile.lalupajusta.ui.screens.CuadernoScreen
import pe.appmobile.lalupajusta.ui.screens.HomeScreen
import pe.appmobile.lalupajusta.ui.screens.OnboardingScreen
import pe.appmobile.lalupajusta.ui.screens.ParentalGateScreen
import pe.appmobile.lalupajusta.ui.screens.PerfilScreen
import pe.appmobile.lalupajusta.ui.viewmodel.CasoViewModel
import pe.appmobile.lalupajusta.ui.viewmodel.CuadernoViewModel
import pe.appmobile.lalupajusta.ui.viewmodel.HomeViewModel
import pe.appmobile.lalupajusta.ui.viewmodel.PerfilViewModel

object Rutas {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CASO = "caso/{casoId}"
    const val PARENTAL_GATE = "parental_gate"
    const val PERFIL = "perfil"
    const val CUADERNO = "cuaderno"
    fun caso(id: String) = "caso/$id"
}

@Composable
fun NavGraph(repository: LupaJustaRepository, esPrimerLanzamiento: Boolean) {
    val navController: NavHostController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = if (esPrimerLanzamiento) Rutas.ONBOARDING else Rutas.HOME) {
        composable(Rutas.ONBOARDING) {
            OnboardingScreen(onTerminar = { alias, avatarId ->
                scope.launch {
                    repository.guardarPerfil(alias, avatarId)
                    navController.navigate(Rutas.HOME) { popUpTo(Rutas.ONBOARDING) { inclusive = true } }
                }
            })
        }
        composable(Rutas.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LifecycleResumeEffect(Unit) {
                viewModel.recargar()
                onPauseOrDispose {}
            }
            HomeScreen(
                uiState = uiState,
                onCasoClick = { navController.navigate(Rutas.caso(it)) },
                onCuadernoClick = { navController.navigate(Rutas.CUADERNO) },
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onAjustesClick = { navController.navigate(Rutas.PARENTAL_GATE) },
            )
        }
        composable(
            Rutas.CASO,
            arguments = listOf(navArgument("casoId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val casoId = backStackEntry.arguments?.getString("casoId") ?: return@composable
            val viewModel: CasoViewModel = viewModel(factory = CasoViewModel.Factory(repository, casoId))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CasoScreen(
                uiState = uiState,
                onTocarPersonaje = viewModel::tocarPersonaje,
                onDeshacerUltimoToque = viewModel::deshacerUltimoToque,
                onConfirmarMuestra = viewModel::confirmarMuestra,
                onAlternarAyuda = viewModel::alternarAyuda,
            )
        }
        composable(Rutas.PERFIL) {
            val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory(repository))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            PerfilScreen(
                uiState = uiState,
                onAliasChange = viewModel::cambiarAlias,
                onAvatarSeleccionado = viewModel::elegirAvatar,
                onGuardar = viewModel::guardar,
            )
        }
        composable(Rutas.PARENTAL_GATE) {
            ParentalGateScreen(repository = repository)
        }
        composable(Rutas.CUADERNO) {
            val viewModel: CuadernoViewModel = viewModel(factory = CuadernoViewModel.Factory(repository))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CuadernoScreen(uiState = uiState)
        }
    }
}

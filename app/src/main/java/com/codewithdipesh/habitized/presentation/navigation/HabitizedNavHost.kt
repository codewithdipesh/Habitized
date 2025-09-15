package com.codewithdipesh.habitized.presentation.navigation

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.codewithdipesh.habitized.presentation.addscreen.AddViewModel
import com.codewithdipesh.habitized.presentation.addscreen.addGoalScreen.AddGoalScreen
import com.codewithdipesh.habitized.presentation.addscreen.addhabitscreen.AddHabitScreen
import com.codewithdipesh.habitized.presentation.drawer.AddWidgetScreen
import com.codewithdipesh.habitized.presentation.drawer.ThoughtsScreen
import com.codewithdipesh.habitized.presentation.goalscreen.GoalDetails
import com.codewithdipesh.habitized.presentation.goalscreen.GoalViewModel
import com.codewithdipesh.habitized.presentation.habitscreen.HabitDetails
import com.codewithdipesh.habitized.presentation.habitscreen.HabitViewModel
import com.codewithdipesh.habitized.presentation.habitscreen.components.ShareProgress
import com.codewithdipesh.habitized.presentation.homescreen.HomeScreen
import com.codewithdipesh.habitized.presentation.homescreen.HomeViewModel
import com.codewithdipesh.habitized.presentation.progress.ProgressScreen
import com.codewithdipesh.habitized.presentation.progress.ProgressViewModel
import com.codewithdipesh.habitized.presentation.timerscreen.durationScreen.DurationViewModel
import com.codewithdipesh.habitized.presentation.timerscreen.durationScreen.DurationScreen
import com.codewithdipesh.habitized.presentation.timerscreen.sessionScreen.SessionScreen
import com.codewithdipesh.habitized.presentation.timerscreen.sessionScreen.SessionViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalEncodingApi::class)
@Composable
fun HabitizedNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    addViewModel: AddViewModel,
    durationViewModel : DurationViewModel,
    sessionViewModel : SessionViewModel,
    progressViewModel : ProgressViewModel,
    habitViewModel : HabitViewModel,
    goalViewModel : GoalViewModel,
    drawerState : DrawerState
){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewmodel = homeViewModel,
                drawerState = drawerState
            )
        }
        composable(
            Screen.AddHabit.route,
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("date"){
                    type = NavType.StringType
                    defaultValue = LocalDate.now().toString()
                }
            )
        ){  entry ->
            val dateStr = entry.arguments?.getString("date")
            val date = LocalDate.parse(dateStr)
            val id = entry.arguments?.getString("id") ?: null
            AddHabitScreen(
                navController = navController,
                viewmodel = addViewModel,
                date= date,
                id = if(id.isNullOrEmpty()) null else UUID.fromString(id)
            )
        }
        composable(
            Screen.DurationScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "com.codewithdipesh.habitized://duration/{id}/{title}/{target}/{color}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                },
                navArgument("title"){
                    type = NavType.StringType
                },
                navArgument("target"){
                    type = NavType.StringType
                },
                navArgument("color"){
                    type = NavType.StringType
                }
            )
        ){ entry ->
            val id = entry.arguments?.getString("id")
            val encodedTitle = entry.arguments?.getString("title") ?: ""
            val title = String(Base64.UrlSafe.decode(encodedTitle))
            val color = entry.arguments?.getString("color")
            val targetSeconds = entry.arguments?.getString("target")!!.toInt()

            val hour = targetSeconds/ 3600
            val minutes = (targetSeconds % 3600) / 60
            val seconds = targetSeconds % 60
            val targetDurationValue = LocalTime.of(hour,minutes,seconds)

            DurationScreen(
                habitProgressId = UUID.fromString(id),
                title = title!!,
                targetDurationValue = targetDurationValue,
                colorKey = color!!,
                navController = navController,
                viewmodel = durationViewModel
            )
        }
        composable(
            Screen.AddGoal.route,
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){entry->
            val id = entry.arguments?.getString("id")
            AddGoalScreen(
                id = if(id.isNullOrEmpty()) null else UUID.fromString(id),
                navController = navController,
                viewmodel = addViewModel
            )
        }
        composable(
            Screen.SessionScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "com.codewithdipesh.habitized://session_screen/{id}/{title}/{target}/{color}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                },
                navArgument("title"){
                    type = NavType.StringType
                },
                navArgument("target"){
                    type = NavType.StringType
                },
                navArgument("color"){
                    type = NavType.StringType
                }
            )
        ){ entry ->
            val id = entry.arguments?.getString("id")
            val encodedTitle = entry.arguments?.getString("title") ?: ""
            val title = String(Base64.UrlSafe.decode(encodedTitle))
            val color = entry.arguments?.getString("color")
            val targetSeconds = entry.arguments?.getString("target")!!.toInt()

            val hour = targetSeconds/ 3600
            val minutes = (targetSeconds % 3600) / 60
            val seconds = targetSeconds % 60
            val targetDurationValue = LocalTime.of(hour,minutes,seconds)

            SessionScreen(
                habitProgressId = UUID.fromString(id),
                title = title!!,
                targetDurationValue = targetDurationValue,
                colorKey = color!!,
                navController = navController,
                viewmodel = sessionViewModel
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                navController = navController,
                viewmodel = progressViewModel
            )
        }
        composable(Screen.MyThoughts.route) {
            ThoughtsScreen(
                navController = navController
            )
        }
        composable(Screen.AddWidget.route) {
            AddWidgetScreen(
                navController = navController
            )
        }
        composable(
            Screen.HabitScreen.route,
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                },
                navArgument("title"){
                    type = NavType.StringType
                },
                navArgument("color"){
                    type = NavType.StringType
                }
            )
        ) {entry->
            val id = entry.arguments?.getString("id")
            val encodedTitle = entry.arguments?.getString("title") ?: ""
            val title = String(Base64.UrlSafe.decode(encodedTitle))
            val color = entry.arguments?.getString("color")

            HabitDetails(
                id = UUID.fromString(id),
                title = title!! ,
                colorKey = color!!,
                navController = navController,
                viewmodel = habitViewModel
            )
        }
        composable(Screen.HabitShareScreen.route){
            ShareProgress(
                navController = navController,
                viewModel = habitViewModel
            )
        }
        composable(
            Screen.GoalScreen.route,
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                },
                navArgument("title"){
                    type = NavType.StringType
                }
            )
        ) {entry->
            val id = entry.arguments?.getString("id")
            val encodedTitle = entry.arguments?.getString("title") ?: ""
            val title = String(Base64.UrlSafe.decode(encodedTitle))

            GoalDetails(
                id = if(id.isNullOrEmpty()) null else UUID.fromString(id),
                title = title!!,
                navController = navController,
                viewmodel = goalViewModel
            )
        }
    }
}
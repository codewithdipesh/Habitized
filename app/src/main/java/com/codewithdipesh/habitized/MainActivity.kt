 package com.codewithdipesh.habitized

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.codewithdipesh.habitized.domain.model.Goal
import com.codewithdipesh.habitized.presentation.addscreen.AddViewModel
import com.codewithdipesh.habitized.presentation.backup.BackupViewModel
import com.codewithdipesh.habitized.presentation.goalscreen.GoalViewModel
import com.codewithdipesh.habitized.presentation.habitscreen.HabitViewModel
import com.codewithdipesh.habitized.presentation.homescreen.HomeViewModel
import com.codewithdipesh.habitized.presentation.navigation.Screen
import com.codewithdipesh.habitized.presentation.homescreen.component.AppDrawer
import com.codewithdipesh.habitized.presentation.homescreen.component.DrawerItem
import com.codewithdipesh.habitized.presentation.navigation.HabitizedNavHost
import com.codewithdipesh.habitized.presentation.progress.ProgressViewModel
import com.codewithdipesh.habitized.presentation.timerscreen.durationScreen.DurationViewModel
import com.codewithdipesh.habitized.presentation.timerscreen.sessionScreen.SessionViewModel
import com.codewithdipesh.habitized.ui.theme.HabitizedTheme
import com.codewithdipesh.habitized.widget.MonthlyHabitWidget
import com.codewithdipesh.habitized.widget.WeeklyHabitWidget
import com.codewithdipesh.habitized.widget.data.HabitWidgetRepository
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.codewithdipesh.habitized.data.sharedPref.HabitPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var habitPreference: HabitPreference

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val introVideoUrl = "https://res.cloudinary.com/ds36qo9ws/video/upload/v1766569902/habit_types-into_sgibua.mov"

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        enableEdgeToEdge()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
                    Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.USE_EXACT_ALARM,
                ),
                0
            )
        }
        setContent {
            HabitizedTheme(){
                val navController = rememberNavController()
                val homeViewModel by viewModels<HomeViewModel>()
                val addViewModel by viewModels<AddViewModel>()
                val durationViewModel by viewModels<DurationViewModel>()
                val sessionViewModel by viewModels<SessionViewModel>()
                val progressViewModel by viewModels<ProgressViewModel>()
                val habitViewModel by viewModels<HabitViewModel>()
                val goalViewModel by viewModels<GoalViewModel>()
                val backupViewModel by viewModels<BackupViewModel>()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                val SecondSectionItems = listOf(
                    DrawerItem(R.drawable.my_thoughts,"My Thoughts"){
                        homeViewModel.getMyThoughts(navController)
                    },
                    DrawerItem(R.drawable.widget,"Add Widgets"){
                        homeViewModel.addWidget(navController)
                    },
                    DrawerItem(R.drawable.backup_icon,"Backup & Restore"){
                        navController.navigate(Screen.BackupScreen.route)
                    },
                    DrawerItem(R.drawable.send_icoc,"Share with Friends"){
                        homeViewModel.shareApp(this)
                    },
                    DrawerItem(R.drawable.report_bug,"Report a bug"){
                        homeViewModel.openMail(this)
                    },
                    DrawerItem(R.drawable.baseline_rate_review_24,"Rate us on PlayStore"){
                        homeViewModel.openPlayStoreForRating(this)
                    },
                    DrawerItem(R.drawable.feedback,"Give Feedback"){
                        homeViewModel.sendFeedback(this)
                    },
                )

                AppDrawer(
                    state = drawerState,
                    secondSectionItems = SecondSectionItems,
                    onFollowClick = {homeViewModel.onFollow(this)},
                    onGithubClick = { homeViewModel.onCodeBase(this)}
                ) {
                    HabitizedNavHost(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        addViewModel = addViewModel,
                        durationViewModel = durationViewModel,
                        sessionViewModel = sessionViewModel,
                        progressViewModel = progressViewModel,
                        habitViewModel = habitViewModel,
                        goalViewModel = goalViewModel,
                        backupViewModel = backupViewModel,
                        drawerState = drawerState,
                        habitPreference = habitPreference,
                        introVideoUrl = introVideoUrl
                    )
                }
            }
        }
    }
     override fun onPause(){
         super.onPause()
         updateAllWidgets()
     }
     override fun onStop() {
         super.onStop()
         updateAllWidgets()
     }

     override fun onDestroy() {
         super.onDestroy()
         updateAllWidgets()

     }

    private fun updateAllWidgets() {
        try {
            lifecycleScope.launch {
                WeeklyHabitWidget().updateAll(applicationContext)
                MonthlyHabitWidget().updateAll(applicationContext)
            }
        } catch (e: Exception) {
            Log.e("WidgetUpdate", "Failed: ${e.message}")
        }
     }
}


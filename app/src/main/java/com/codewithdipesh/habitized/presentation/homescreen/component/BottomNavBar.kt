package com.codewithdipesh.habitized.presentation.homescreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.presentation.navigation.Screen

@Composable
fun BottomNavBar(
    selectedScreen: Screen,
    onAddClick: () -> Unit = {},
    onNavigate: (Screen) -> Unit = {},
    modifier : Modifier = Modifier
) {
    Surface{
        NavigationBar(
            modifier = modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedScreen == Screen.Home,
                onClick = { onNavigate(Screen.Home) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.home_filled),
                        contentDescription = "Home",
                        modifier = Modifier
                            .height(30.dp)
                            .padding(8.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.onTertiary,
                    selectedIconColor = MaterialTheme.colorScheme.inverseOnSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary
                )

            )

            // Spacer for central Add button
            Spacer(modifier = Modifier.width(8.dp))

            // Custom Add button (centered manually)
            AnimatedVisibility(
                visible = selectedScreen == Screen.Home
            ) {
                Box(modifier = Modifier
                    .size(48.dp)
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onAddClick() }, contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
            if(selectedScreen != Screen.Home){
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            NavigationBarItem(
                selected = selectedScreen == Screen.Progress,
                onClick = { onNavigate(Screen.Progress) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.habit_filled),
                        contentDescription = "Progress",
                        modifier = Modifier
                            .height(30.dp)
                            .padding(8.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.onTertiary,
                    selectedIconColor = MaterialTheme.colorScheme.inverseOnSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

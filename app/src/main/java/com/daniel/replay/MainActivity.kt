package com.daniel.replay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daniel.replay.navigation.Screens
import com.daniel.replay.navigation.SetupNavGraph
import com.daniel.replay.ui.theme.REPLAYTheme
import com.daniel.replay.ui.viewmodels.HomeViewModel
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    companion object{
        init {
            Shell.enableVerboseLogging = true
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setFlags(Shell.FLAG_MOUNT_MASTER) //Mount Master to access the storage, It's the "-mm" tag in Linux.
                    .setTimeout(10)
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Preheating the main root shell so the app can use it afterwards without interrupting application flow (e.g. root permission prompt)
        Shell.getShell()

        setContent {
            REPLAYTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    startDestination = Screens.Home.route
                )
            }
        }
    }
}

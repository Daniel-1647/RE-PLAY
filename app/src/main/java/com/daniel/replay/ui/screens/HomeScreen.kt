package com.daniel.replay.ui.screens

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daniel.replay.ui.viewmodels.HomeViewModel
import com.daniel.replay.R
import com.daniel.replay.ui.components.CustomAlertDialog
import com.daniel.replay.ui.components.DeleteAllSavesAlertDialog
import com.daniel.replay.ui.components.GameCard
import com.daniel.replay.ui.components.SaveMenu
import com.daniel.replay.ui.components.SearchBar
import com.daniel.replay.ui.theme.OnSurface
import com.daniel.replay.ui.theme.Outline
import com.daniel.replay.ui.theme.Surface
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun HomeScreen(navController: NavHostController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val mContext = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val activity = (mContext as? Activity)
    val rootAccessError by homeViewModel.rootAccessError.collectAsState()

    val customAlert by homeViewModel.customAlert.collectAsState()
    val customAlertTxt by homeViewModel.customAlertTxt.collectAsState()
    val customAlertTitle by homeViewModel.customAlertTitle.collectAsState()

    val deleteAllSavesAlert by homeViewModel.deleteAllSavesAlert.collectAsState()
    val deleteAllSavesAlertTxt by homeViewModel.deleteAllSavesAlertTxt.collectAsState()
    val deleteAllSavesAlertTitle by homeViewModel.deleteAllSavesAlertTitle.collectAsState()

    val choicescriptGames by homeViewModel.choicescriptGames.collectAsState()
    val saveMenuLoaded by homeViewModel.saveMenuLoaded.collectAsState()
    var pkgName by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true){
        homeViewModel.checkForRootAccess()
        homeViewModel.getChoiceScriptGames()
    }

    //Observe for root access error
    if (rootAccessError){
        //This alert box gets executed in case the application is unable to get root access.
        CustomAlertDialog(
            onDismissRequest = {},
            dialogTitle = stringResource(R.string.root_access_alert_title),
            dialogText = stringResource(R.string.root_access_alert_text),
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFBA000D))
            },
            confirmationTxt = "Exit",
            onConfirmation = {
                exitProcess(0)
            }
        )
    }

    //Observe for custom alerts
    if (customAlert){
        //This alert box gets executed in case there are some errors.
        CustomAlertDialog(
            onDismissRequest = {},
            dialogTitle = customAlertTitle,
            dialogText = customAlertTxt,
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFBA000D))
            },
            confirmationTxt = "Ok",
            onConfirmation = {
                homeViewModel.dismissCustomAlert()
            }
        )
    }

    //Observe for delete all save alert
    if (deleteAllSavesAlert){
        DeleteAllSavesAlertDialog(
            onDismissRequest = {
                homeViewModel.dismissDeleteAllSavesAlert()
            },
            dialogTitle = deleteAllSavesAlertTitle,
            dialogText = deleteAllSavesAlertTxt,
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFBA000D))
            },
            confirmationTxt = "Yes",
            dismissTxt = "No",
            onConfirmation = {
                homeViewModel.deleteAllSaves(pkgName)
                homeViewModel.dismissDeleteAllSavesAlert()
            }
        )
    }

    if (saveMenuLoaded){
        SaveMenu(pkgName, homeViewModel)
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Re:Play",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = OnSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Surface),
                actions = {
//                    IconButton(
//                        onClick = { /*TODO*/ }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Sharp.Settings,
//                            contentDescription = null,
//                            tint = OnSurface
//                        )
//                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    searchText = searchText,
                    onValueChange = {
                        searchText = it
                        homeViewModel.filterAndSortChoicescriptGames(it)
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                Divider(
                    color = Outline
                )
                GamesList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 12.dp, start = 12.dp, top = 12.dp),
                    choicescriptGames = choicescriptGames,
                    packageManager = mContext.packageManager,
                    onClick = {
                        pkgName = it
                        homeViewModel.loadSaveMenu()
                    }
                )
            }
        }
    }
}

@Composable
fun GamesList(
    modifier: Modifier = Modifier,
    choicescriptGames: List<ApplicationInfo>?,
    packageManager: PackageManager,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        choicescriptGames?.forEach {
            item{
                GameCard(
                    icon = it.loadIcon(packageManager),
                    name = packageManager.getApplicationLabel(it).toString(),
                    packageName = it.packageName,
                    onClick = {
                        onClick(it.packageName)
                    }
                )
            }
        }
        item{
            Spacer(modifier = Modifier
                .height(4.dp)
                .fillMaxWidth())
        }
    }
}

@Preview
@Composable
fun HomePrev() {
    HomeScreen(rememberNavController())
}
package com.daniel.replay.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.replay.data.SaveData
import com.daniel.replay.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveMenu(pkgName: String, homeViewModel: HomeViewModel) {
    val saveFiles by homeViewModel.saveFiles.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    homeViewModel.updateSaves(pkgName) //Initialize the saveFiles

    AlertDialog(onDismissRequest = {
        homeViewModel.clearSaves()
        homeViewModel.dismissSaveMenu()
    }) {
        Surface(color = Color(0xFF4f5f7d)){
            Column() {
                TitleBar()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    state = listState
                ) {
                    saveFiles?.forEach {
                        item {
                            SaveItem(
                                saveName = it.saveName,
                                saveTime = it.saveTime,
                                pkgName = it.pkgName,
                                onDeleteSave = {
                                    homeViewModel.deleteSave(it.saveName, pkgName)
                                },
                                onRestoreSave = {
                                    homeViewModel.restoreSave(it.saveName, pkgName)
                                }
                            )
                        }
                    }
                }
                BottomMenu(
                    onClickBackup = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(saveFiles.count())
                        }
                        homeViewModel.backupSave(pkgName)
                    },
                    deleteAllSaves = {
                        homeViewModel.setDeleteAllSavesAlert(
                            title = "Alert!",
                            msg = "Are you sure that you would like to delete all the saves?",
                            onConfirmation = { homeViewModel.deleteAllSaves(pkgName) }
                        )
                    },
                    startApp = {
                        homeViewModel.launchGame(pkgName)
                    })
            }
        }
    }
}

@Composable
fun TitleBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = Icons.Filled.Assignment,
            contentDescription = null,
            tint = Color(0xFF191c1c)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Save Menu",
            fontSize = 28.sp,
            color = Color(0xFF191c1c),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BottomMenu(onClickBackup: () -> Unit, deleteAllSaves: () -> Unit, startApp: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Button(
            modifier = Modifier.fillMaxHeight(),
            shape = RectangleShape,
            onClick = onClickBackup,
            colors = ButtonDefaults.buttonColors(Color(0xFF091b36))
        ){
            Text(
                text = "Backup"
            )
        }
        Button(
            modifier = Modifier.fillMaxHeight(),
            shape = RectangleShape,
            onClick = startApp,
            colors = ButtonDefaults.buttonColors(Color(0xFF051f21))
        ){
            Text(
                text = "Start App"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            shape = RectangleShape,
            onClick = deleteAllSaves,
            colors = ButtonDefaults.buttonColors(Color(0xFFBA000D))
        ){
            Text("Delete All")
        }
    }
}

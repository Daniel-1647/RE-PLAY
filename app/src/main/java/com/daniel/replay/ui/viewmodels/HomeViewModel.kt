package com.daniel.replay.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.daniel.replay.R
import com.daniel.replay.data.SaveData
import com.daniel.replay.service.*
import com.daniel.replay.utility.epochTimeToStandardTime
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context
):ViewModel() {
    val _rootAccessError = MutableStateFlow(false)
    val rootAccessError: StateFlow<Boolean> = _rootAccessError.asStateFlow()

    val _customAlert = MutableStateFlow(false)
    val customAlert: StateFlow<Boolean> = _customAlert.asStateFlow()

    val _customAlertTxt = MutableStateFlow("")
    val customAlertTxt: StateFlow<String> = _customAlertTxt.asStateFlow()

    val _customAlertTitle = MutableStateFlow("")
    val customAlertTitle: StateFlow<String> = _customAlertTitle.asStateFlow()

    val _deleteAllSavesAlert = MutableStateFlow(false)
    val deleteAllSavesAlert: StateFlow<Boolean> = _deleteAllSavesAlert.asStateFlow()

    val _deleteAllSavesAlertTxt = MutableStateFlow("")
    val deleteAllSavesAlertTxt: StateFlow<String> = _deleteAllSavesAlertTxt.asStateFlow()

    val _deleteAllSavesAlertTitle = MutableStateFlow("")
    val deleteAllSavesAlertTitle: StateFlow<String> = _deleteAllSavesAlertTitle.asStateFlow()

    val _choicescriptGames = MutableStateFlow<List<ApplicationInfo>?>(null)
    val choicescriptGames: StateFlow<List<ApplicationInfo>?> = _choicescriptGames.asStateFlow()

    val _saveMenuLoaded = MutableStateFlow(false)
    val saveMenuLoaded: StateFlow<Boolean> = _saveMenuLoaded.asStateFlow()

    val _saveFiles = MutableStateFlow<List<SaveData>>(emptyList())
    val saveFiles: StateFlow<List<SaveData>> = _saveFiles.asStateFlow()

    fun checkForRootAccess(){
        _rootAccessError.value = !Shell.isAppGrantedRoot()!! || Shell.isAppGrantedRoot() == null
    }

    fun dismissSaveMenu(){
        _saveMenuLoaded.value = false
    }

    fun loadSaveMenu() {
        _saveMenuLoaded.value = true
    }

    fun setCustomAlert(title: String, msg: String){
        _customAlertTitle.value = title
        _customAlertTxt.value = msg
        _customAlert.value = true
    }

    fun dismissCustomAlert(){
        _customAlert.value = false
    }

    fun setDeleteAllSavesAlert(title: String, msg: String, onConfirmation: () -> Unit){
        _deleteAllSavesAlertTitle.value = title
        _deleteAllSavesAlertTxt.value = msg
        _deleteAllSavesAlert.value = true
    }

    fun dismissDeleteAllSavesAlert(){
        _deleteAllSavesAlert.value = false
    }

    fun getChoiceScriptGames(){
        val apps = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            applicationContext.packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
        }else{
            applicationContext.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }

        // Filter and sort packages
        val filteredPackages = apps.filter { applicationInfo ->
            applicationInfo.packageName.contains("org.hostedgames") || applicationInfo.packageName.contains("com.choiceofgames")
        }.sortedBy { packageInfo ->
            applicationContext.packageManager.getApplicationLabel(packageInfo).toString().lowercase(
                Locale.ROOT
            ) // Convert to lowercase for case-insensitive sorting
        }

//        for (packageInfo in filteredPackages) {
//            LogService.getInstance().LOGD("Pkg Name: " + packageInfo.packageName + " Name: " + applicationContext.packageManager.getApplicationLabel(packageInfo))
//        }

        _choicescriptGames.value = filteredPackages
    }

    fun filterAndSortChoicescriptGames(searchText: String) {
        getChoiceScriptGames() //to update the packages every time the function gets called

        val filteredList = _choicescriptGames.value?.filter { appInfo ->
            (appInfo.packageName.contains("org.hostedgames", ignoreCase = true) || appInfo.packageName.contains("com.choiceofgames", ignoreCase = true)) &&
                    (searchText.isBlank() || applicationContext.packageManager.getApplicationLabel(appInfo).toString().contains(searchText, ignoreCase = true))
        }?.sortedBy { appInfo ->
            applicationContext.packageManager.getApplicationLabel(appInfo).toString().lowercase(Locale.ROOT)
        }
        _choicescriptGames.value = filteredList
    }

    @SuppressLint("SdCardPath")
    fun updateSaves(pkgName: String) {
        val savesPath = applicationContext.resources.getString(R.string.saves_path)
        if(!doesPathExists(savesPath)){
            if (!mkDir(savesPath)){
                setCustomAlert("Error!", "Unable to create directory $savesPath")
            } else {
                updateSaves(pkgName)
                return
            }
        } else {
            if (!doesPathExists(pkgName)){
                if (!mkDir(savesPath+pkgName)){
                    setCustomAlert("Error!", "Unable to create directory $savesPath")
                } else {
                    val files = listAllFiles(savesPath+pkgName)
                    if (files.isNotEmpty()){
                        var saveFiles = files.map { fileName ->
                            val saveTime = epochTimeToStandardTime(getEpochTime("$savesPath$pkgName/$fileName")) // Converting epoch time to standard time
                            val saveData = SaveData(fileName, saveTime, pkgName)
                            saveData
                        }
                        saveFiles = saveFiles.sortedWith(compareBy {
                            val numPart = it.saveName.removePrefix("Save").toIntOrNull()
                            numPart ?: Int.MAX_VALUE // Treat non-numeric names as maximum value to push them to the end
                        })
                        _saveFiles.value = saveFiles
                    } else {
                        _saveFiles.value = emptyList()
                    }
                }
            }
        }
    }

    @SuppressLint("SdCardPath")
    fun backupSave(pkgName: String) {
        val savesPath = applicationContext.resources.getString(R.string.saves_path)
        val files = listAllFiles(savesPath + pkgName)
        val newSaveName = if (files.isEmpty()) {
            "Save1" // If no saves exist, use Save1
        } else {
            // Find the maximum number in the existing save names and increment by 1
            val lastSaveNumber = files.mapNotNull { fileName ->
                // Extract the number from the save name
                val matchResult = Regex("""Save(\d+)""").find(fileName)
                matchResult?.groupValues?.getOrNull(1)?.toIntOrNull()
            }.maxOrNull() ?: 0
            "Save${lastSaveNumber + 1}"
        }

        if (!doesPathExists(savesPath+pkgName)){
            mkDir(savesPath+pkgName)
            backupSave(pkgName)
            return
        }
        if (!copyFile("/data/data/$pkgName/shared_prefs/settings.xml", "$savesPath$pkgName/")) {
            setCustomAlert("Error!", "Unable to locate a save file.")
        } else {
            renameFile("$savesPath$pkgName/settings.xml", newSaveName)
            updateSaves(pkgName)
        }
    }

    fun deleteSave(saveName: String, pkgName: String) {
        val savesPath = applicationContext.resources.getString(R.string.saves_path)
        if(deleteFile("$savesPath$pkgName/$saveName")){
            updateSaves(pkgName)
        } else {
            setCustomAlert("Error!", "Unable to remove save.")
        }
    }

    @SuppressLint("SdCardPath")
    fun restoreSave(saveName: String, pkgName: String){
        killProcess(pkgName) //Need to kill the game before restoring save
        val savesPath = applicationContext.resources.getString(R.string.saves_path)
        deleteFile("/data/data/$pkgName/shared_prefs/settings.xml")
        copyFile("$savesPath$pkgName/$saveName", "/data/data/$pkgName/shared_prefs/")
        renameFile("/data/data/$pkgName/shared_prefs/$saveName", "settings.xml")
        chmod("/data/data/$pkgName/shared_prefs/settings.xml", "777")
    }

    fun deleteAllSaves(pkgName: String){
        val savesPath = applicationContext.resources.getString(R.string.saves_path)
        if(deleteAllContents("$savesPath$pkgName/")){
            updateSaves(pkgName)
        } else {
            setCustomAlert("Error!", "Unable to remove saves.")
        }
    }

    fun clearSaves() {
        _saveFiles.value = emptyList()
    }

    fun launchGame(pkgName: String){
        if (launchApp(pkgName)){
            Toast.makeText(applicationContext, "Game launched successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Unable to launch application!", Toast.LENGTH_SHORT).show()
        }
    }

}

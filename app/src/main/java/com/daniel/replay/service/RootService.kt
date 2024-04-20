package com.daniel.replay.service

import com.topjohnwu.superuser.Shell

fun doesPathExists(path: String): Boolean {
    val result: Shell.Result = Shell.cmd("test -d \"$path\"").exec()
    return result.isSuccess
}

fun copyFile(sourcePath: String, destinationPath: String): Boolean {
    val result: Shell.Result = Shell.cmd("cp \"$sourcePath\" \"$destinationPath\"").exec()
    return result.isSuccess
}

fun renameFile(filePath: String, newFileName: String): Boolean {
    val parentDirectory = filePath.substringBeforeLast('/')
    val result: Shell.Result = Shell.cmd("mv \"$filePath\" \"$parentDirectory/$newFileName\"").exec()
    return result.isSuccess
}

fun launchApp(pkgName: String): Boolean {
    val result: Shell.Result = Shell.cmd("monkey -p $pkgName -c android.intent.category.LAUNCHER 1").exec()
    return result.isSuccess
}

fun mkDir(path: String): Boolean {
    val result: Shell.Result = Shell.cmd("mkdir -p \"$path\"").exec()
    return result.isSuccess
}

fun deleteFile(path: String): Boolean {
    val result: Shell.Result = Shell.cmd("rm $path").exec()
    return result.isSuccess
}

fun killProcess(pkgName: String) {
    val result: Shell.Result = Shell.cmd("pidof $pkgName").exec()
    if (result.out.isNotEmpty()) {
        val pid = result.out[0].toString()
        Shell.cmd("kill $pid").exec()
    }
}

fun chmod(path: String, permissions: String){
    val result: Shell.Result = Shell.cmd("chmod $permissions $path").exec()
}

fun deleteAllContents(path: String): Boolean{
    val result: Shell.Result = Shell.cmd("rm -rf $path*").exec()
    return result.isSuccess
}

fun listAllFiles(path: String): List<String> {
    val result: Shell.Result = Shell.cmd("ls $path").exec()
    return result.out
}

fun getEpochTime(path: String): String{
    val result: Shell.Result = Shell.cmd("stat -c '%Y' $path").exec()
    return result.out[0]
}
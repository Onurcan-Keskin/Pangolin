package com.onurcan.pangolin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onurcan.pangolin.ui.mvp.executeAsyncTask
import kotlinx.coroutines.delay

class SettingsViewModel: ViewModel() {
    fun populateFirebaseTask(){
        viewModelScope.executeAsyncTask(
            onPreExecute = {
                // ... runs in Main Thread
            }, doInBackground = { publishProgress: suspend (progress: Int) -> Unit ->

                // ... runs in Background Thread

                // simulate progress update
                publishProgress(50) // call `publishProgress` to update progress, `onProgressUpdate` will be called
                delay(1000)
                publishProgress(100)


                "Result" // send data to "onPostExecute"
            }, onPostExecute = {
                // runs in Main Thread
                // ... here "it" is a data returned from "doInBackground"
            }, onProgressUpdate = {
                // runs in Main Thread
                // ... here "it" contains progress
            }
        )
    }
}
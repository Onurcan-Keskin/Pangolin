/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.onurcan.exovideoreference.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun showLogDebug(tag:String,string: String) {
    Log.d(tag,string)
}

fun showLogInfo(tag:String,string: String) {
    Log.i(tag,string)
}

fun showLogWarning(tag: String,string: String){
    Log.w(tag,string)
}

fun showLogError(tag: String,string: String){
    Log.e(tag,string)
}

fun showSnackbar(view:View,string: String) {
    Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show()
}

fun showToast(context:Context, string : String){
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}
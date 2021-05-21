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

package com.onurcan.pangolin.helpers

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.webkit.URLUtil
import com.onurcan.exovideoreference.utils.showLogInfo

class UrlValidatorHelper : TextWatcher {

    internal var isValid = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        showLogInfo(this.javaClass.simpleName,s.toString())
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        showLogInfo(this.javaClass.simpleName,s.toString())
    }

    override fun afterTextChanged(s: Editable?) {
        isValid = isValidUrl(s)
    }

    companion object {

        fun isValidUrl(url: CharSequence?): Boolean {
            return url!=null && URLUtil.isValidUrl(url.trim().toString()) && Patterns.WEB_URL.matcher(url).matches()
        }
    }
}
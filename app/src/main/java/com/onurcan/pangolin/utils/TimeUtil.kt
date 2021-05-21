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

package com.onurcan.pangolin.utils

import com.onurcan.pangolin.helpers.Constants
import java.util.*

class TimeUtil {

    /**
     * ms to 00:00:00
     * @param time ms
     * @return String
     */
    fun formatLongToTimeStr(time: Int): String {
        val totalSeconds = time / Constants.MS_TO_SECOND
        val seconds = totalSeconds % Constants.SECOND_TO_MINUTE
        var minutes = totalSeconds / Constants.SECOND_TO_MINUTE
        val hours = totalSeconds / Constants.SECOND_TO_HOUR

        return if (hours > 0) {
            minutes %= Constants.SECOND_TO_MINUTE
            String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
        }
    }

}
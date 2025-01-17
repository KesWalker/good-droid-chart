/*
 * Copyright 2022 Patryk Goworowski and Patryk Michalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatryk.vico.view.gestures

import android.view.MotionEvent
import kotlin.math.abs

internal val MotionEvent.movedXDistance: Float
    get() = if (historySize > 0) abs(x - getHistoricalX(historySize - 1)) else 0f

internal val MotionEvent.movedYDistance: Float
    get() = if (historySize > 0) abs(y - getHistoricalY(historySize - 1)) else 0f

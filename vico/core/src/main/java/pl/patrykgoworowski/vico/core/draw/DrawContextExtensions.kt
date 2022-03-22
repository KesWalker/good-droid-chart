/*
 * Copyright (c) 2021. Patryk Goworowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.patrykgoworowski.vico.core.draw

import android.graphics.Canvas
import android.graphics.RectF
import pl.patrykgoworowski.vico.core.DefaultColors
import pl.patrykgoworowski.vico.core.axis.model.ChartModel
import pl.patrykgoworowski.vico.core.axis.model.MutableChartModel
import pl.patrykgoworowski.vico.core.context.DefaultExtras
import pl.patrykgoworowski.vico.core.context.DrawContext
import pl.patrykgoworowski.vico.core.context.Extras

/**
 * Calls the specified function block with [DrawContext.canvas] as its receiver.
 */
public inline fun DrawContext.withCanvas(block: Canvas.() -> Unit) {
    canvas.block()
}

/**
 * Creates an anonymous implementation of [DrawContext].
 *
 * @param canvas the canvas to draw the chart on.
 * @param density the pixel density of the screen (used in pixel size calculation).
 * @param fontScale the scale of fonts.
 * @param isLtr whether the device layout is left-to-right.
 * @param elevationOverlayColor the elevation overlay color. This is applied to components that cast shadows.
 */
public fun drawContext(
    canvas: Canvas,
    density: Float,
    fontScale: Float,
    isLtr: Boolean,
    elevationOverlayColor: Long = DefaultColors.Light.elevationOverlayColor,
): DrawContext = object : DrawContext, Extras by DefaultExtras() {
    override val canvasBounds: RectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
    override val chartModel: ChartModel = MutableChartModel()
    override val elevationOverlayColor: Long = elevationOverlayColor
    override var canvas: Canvas = canvas
    override val density: Float = density
    override val fontScale: Float = fontScale
    override val isLtr: Boolean = isLtr
    override val isHorizontalScrollEnabled: Boolean = false
    override val horizontalScroll: Float = 0f
    override val chartScale: Float = 1f

    override fun withOtherCanvas(canvas: Canvas, block: (DrawContext) -> Unit) {
        val originalCanvas = this.canvas
        this.canvas = canvas
        block(this)
        this.canvas = originalCanvas
    }
}

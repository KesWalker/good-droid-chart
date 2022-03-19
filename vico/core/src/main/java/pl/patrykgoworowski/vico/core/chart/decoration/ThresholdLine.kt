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

package pl.patrykgoworowski.vico.core.chart.decoration

import android.graphics.RectF
import java.text.DecimalFormat
import pl.patrykgoworowski.vico.core.DefaultDimens
import pl.patrykgoworowski.vico.core.chart.draw.ChartDrawContext
import pl.patrykgoworowski.vico.core.component.shape.ShapeComponent
import pl.patrykgoworowski.vico.core.component.shape.rectComponent
import pl.patrykgoworowski.vico.core.component.text.HorizontalPosition
import pl.patrykgoworowski.vico.core.component.text.TextComponent
import pl.patrykgoworowski.vico.core.component.text.VerticalPosition
import pl.patrykgoworowski.vico.core.component.text.buildTextComponent
import pl.patrykgoworowski.vico.core.context.MeasureContext
import pl.patrykgoworowski.vico.core.extension.ceil
import pl.patrykgoworowski.vico.core.extension.floor
import pl.patrykgoworowski.vico.core.extension.half
import pl.patrykgoworowski.vico.core.extension.median

/**
 * The [ThresholdLine] is drawn on top of the chart and marks a certain range of y-axis values.
 *
 * @property thresholdRange the range of y-axis values which this [ThresholdLine] will cover.
 * @property thresholdLabel the label of this [ThresholdLine].
 * @property lineComponent the [ShapeComponent] drawn as the threshold line.
 * @property minimumLineThicknessDp the minimal thickness of the threshold line. If [thresholdRange] covered in pixels
 * is smaller than this value, the [minimumLineThicknessDp] will be used as threshold line’s thickness.
 * @property labelComponent the [TextComponent] used to draw the [thresholdLabel] text.
 * @property labelHorizontalPosition defines the horizontal position of the label.
 * @property labelVerticalPosition defines the vertical position of the label.
 *
 * @see Decoration
 */
public data class ThresholdLine(
    val thresholdRange: ClosedFloatingPointRange<Float>,
    val thresholdLabel: CharSequence = RANGE_FORMAT.format(
        decimalFormat.format(thresholdRange.start),
        decimalFormat.format(thresholdRange.endInclusive),
    ),
    val lineComponent: ShapeComponent = rectComponent(),
    val minimumLineThicknessDp: Float = DefaultDimens.THRESHOLD_LINE_THICKNESS,
    val labelComponent: TextComponent = buildTextComponent(),
    val labelHorizontalPosition: LabelHorizontalPosition = LabelHorizontalPosition.Start,
    val labelVerticalPosition: LabelVerticalPosition = LabelVerticalPosition.Top,
) : Decoration {

    /**
     * The alternative constructor allowing usage of single value on y-axis.
     *
     * @property thresholdValue the single value on y-axis which this [ThresholdLine] will cover.
     * @property thresholdLabel the label of this [ThresholdLine].
     * @property lineComponent the [ShapeComponent] drawn as the threshold line.
     * @property minimumLineThicknessDp the minimal thickness of the threshold line. If [thresholdRange] covered
     * in pixels is smaller than this value, the [minimumLineThicknessDp] will be used as threshold line’s thickness.
     * @property labelComponent the [TextComponent] used to draw the [thresholdLabel] text.
     * @property labelHorizontalPosition defines the horizontal position of the label.
     * @property labelVerticalPosition defines the vertical position of the label.
     */
    public constructor(
        thresholdValue: Float,
        thresholdLabel: CharSequence = decimalFormat.format(thresholdValue),
        lineComponent: ShapeComponent = rectComponent(),
        minimumLineThicknessDp: Float = DefaultDimens.THRESHOLD_LINE_THICKNESS,
        textComponent: TextComponent = buildTextComponent(),
        labelHorizontalPosition: LabelHorizontalPosition = LabelHorizontalPosition.Start,
        labelVerticalPosition: LabelVerticalPosition = LabelVerticalPosition.Top,
    ) : this(
        thresholdRange = thresholdValue..thresholdValue,
        thresholdLabel = thresholdLabel,
        lineComponent = lineComponent,
        minimumLineThicknessDp = minimumLineThicknessDp,
        labelComponent = textComponent,
        labelHorizontalPosition = labelHorizontalPosition,
        labelVerticalPosition = labelVerticalPosition,
    )

    override fun draw(
        context: ChartDrawContext,
        bounds: RectF,
    ): Unit = with(context) {
        val valueRange = chartModel.maxY - chartModel.minY

        val centerY = bounds.bottom - thresholdRange.median / valueRange * bounds.height()

        val topY = minOf(
            bounds.bottom - thresholdRange.endInclusive / valueRange * bounds.height(),
            centerY - minimumLineThicknessDp.pixels.half,
        ).ceil
        val bottomY = maxOf(
            bounds.bottom - thresholdRange.start / valueRange * bounds.height(),
            centerY + minimumLineThicknessDp.pixels.half,
        ).floor
        val textY = when (labelVerticalPosition) {
            LabelVerticalPosition.Top -> topY
            LabelVerticalPosition.Bottom -> bottomY
        }

        lineComponent.draw(
            context = context,
            left = bounds.left,
            right = bounds.right,
            top = topY,
            bottom = bottomY
        )
        labelComponent.drawText(
            context = context,
            text = thresholdLabel,
            maxTextWidth = bounds.width().toInt(),
            textX = when (labelHorizontalPosition) {
                LabelHorizontalPosition.Start -> bounds.left
                LabelHorizontalPosition.End -> bounds.right
            },
            textY = textY,
            horizontalPosition = labelHorizontalPosition.position,
            verticalPosition = getSuggestedLabelVerticalPosition(context, bounds, thresholdLabel, textY).position,
        )
    }

    private fun getSuggestedLabelVerticalPosition(
        context: MeasureContext,
        bounds: RectF,
        text: CharSequence,
        textY: Float,
    ): LabelVerticalPosition {
        val labelHeight = labelComponent.getHeight(context = context, text = text)
        return when (labelVerticalPosition) {
            LabelVerticalPosition.Top ->
                if (textY - labelHeight < bounds.top) LabelVerticalPosition.Bottom else labelVerticalPosition
            LabelVerticalPosition.Bottom ->
                if (textY + labelHeight > bounds.bottom) LabelVerticalPosition.Top else labelVerticalPosition
        }
    }

    /**
     * Defines the horizontal position of the label.
     *
     * @property position the [HorizontalPosition] used under the hood for drawing the label.
     */
    public enum class LabelHorizontalPosition(public val position: HorizontalPosition) {
        Start(HorizontalPosition.Start),
        End(HorizontalPosition.End),
    }

    /**
     * Defines the vertical position of the label.
     *
     * @property position the [VerticalPosition] used under the hood for drawing the label.
     */
    public enum class LabelVerticalPosition(public val position: VerticalPosition) {
        Top(VerticalPosition.Bottom),
        Bottom(VerticalPosition.Top),
    }

    private companion object {
        const val RANGE_FORMAT = "%s–%s"
        val decimalFormat = DecimalFormat("#.##")
    }
}

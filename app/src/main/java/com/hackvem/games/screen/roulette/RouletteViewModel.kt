package com.hackvem.games.screen.roulette

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.math.atan2

@HiltViewModel
class RouletteViewModel @Inject constructor(): ViewModel() {
    private val rotateCount = 20    // count of rotating

    /**
     * getTargetAngle
     * @param targetIndex Int
     *
     * get target angle randomly
     * if targetIndex is positive digit,
     * specify angle for target. otherwise get random angle
     */
    fun getTargetAngle(targetIndex: Int, angle: Int): Float {
        val result = if (targetIndex >= 0) {
            val maxAngle = (targetIndex + 1) * angle
            rand(maxAngle, maxAngle - angle + 1)
        } else {
            rand(ROUND_ANGLE.toInt())
        } + ROUND_ANGLE * rotateCount.toFloat()

        return result
    }

    /**
     * random
     * @param to Int
     * @param from Int
     */
    fun rand(to: Int, from: Int = 0): Int = Random().nextInt(to - from) + from

    /**
     * getIndexFromAngle
     * returns index of result calculated
     * @param offset offset from tapped position
     * @param size screen size
     * @param angle angle that each item occupying
     */
    fun getIndexFromAngle(
        offset: Offset,
        size: IntSize,
        angle: Float
    ): Int {
        val touchX = offset.x
        val touchY = offset.y

        val centerX = size.width / 2f // X-coordinate of the center
        val centerY = size.height / 2f // Y-coordinate of the center

        val dx = touchX - centerX // Difference in X-coordinates
        val dy = touchY - centerY // Difference in Y-coordinates

        // Calculate the angle using the arctan2 function
        val angleRadians = atan2(dx, dy).toFloat()

        // Convert the angle to degrees
        var angleDegrees = Math
            .toDegrees(angleRadians.toDouble())
            .toFloat()

        // Wrap the angle within the range of 0 to 360 degrees
        if (angleDegrees < 0) {
            angleDegrees += ROUND_ANGLE
        }

        val map = if (angleDegrees >= 180) {
            angleDegrees - 180f
        } else {
            angleDegrees + 180f
        }

        /**
         * when a section of roulette selected
         * it launches and calculate which target it is
         * then process let user input the target name indicating
         */
        val selectedIndex = (map / angle).toInt()
        println("selectedIndex: $selectedIndex")

        return selectedIndex
    }

    /**
     * getResultIndex
     * from targetValue and angle, calculate
     */
    fun getResultIndex(targetValue: Float, angle: Float): Int {
        return ((targetValue - (rotateCount * ROUND_ANGLE)) / angle).toInt()
    }

    companion object {
        const val ROUND_ANGLE = 360f
        const val MIN_CANDIDATE = 2
        const val MAX_CANDIDATE = 8

        val COLOR_LIST = listOf(
            Color.Red.copy(blue = 0.1f, green = 0.2f),
            Color.Magenta,
            Color.Yellow,
            Color.Green,
            Color.Blue.copy(green = 0.5f),
            Color.Cyan,
            Color.Yellow.copy(red = 0.1f, blue = 0.4f, green = 0.6f),
            Color.Red.copy(red = 0.6f, blue = 0.3f)
        )
    }
}
package com.hackvem.games.screen.roulette

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.atan2

@HiltViewModel
class RouletteViewModel @Inject constructor(): ViewModel() {

    /**
     * getIndexFromAngle
     * returns index of result calculated
     */
    fun getIndexFromAngle(
        tapGesture: Offset,
        size: IntSize,
        angle: Float
    ): Int {
        val touchX = tapGesture.x
        val touchY = tapGesture.y

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
            angleDegrees += 360f
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
}
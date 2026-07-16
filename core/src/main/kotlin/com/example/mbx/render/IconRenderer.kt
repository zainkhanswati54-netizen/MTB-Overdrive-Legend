package com.example.mbx.render

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

object IconRenderer {

    /** Simple two-wheel bike + crouched rider silhouette, drawn with lines/circles. */
    fun drawBike(sr: ShapeRenderer, cx: Float, cy: Float, size: Float, color: Color) {
        val wheelR = size * 0.16f
        val rearX = cx - size * 0.28f
        val frontX = cx + size * 0.32f
        val wheelY = cy - size * 0.28f

        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(color)
        circle(sr, rearX, wheelY, wheelR)
        circle(sr, frontX, wheelY, wheelR)

        val seatX = cx - size * 0.10f
        val seatY = cy + size * 0.02f
        val pedalX = cx
        val pedalY = cy - size * 0.20f
        val handleX = cx + size * 0.20f
        val handleY = cy + size * 0.06f

        sr.line(rearX, wheelY, seatX, seatY)
        sr.line(seatX, seatY, pedalX, pedalY)
        sr.line(pedalX, pedalY, rearX, wheelY)
        sr.line(pedalX, pedalY, frontX, wheelY)
        sr.line(pedalX, pedalY, handleX, handleY)
        sr.line(handleX, handleY, frontX, wheelY)

        // Rider torso + head
        val headCx = cx + size * 0.10f
        val headCy = cy + size * 0.30f
        circle(sr, headCx, headCy, size * 0.075f)
        sr.line(headCx - size * 0.02f, headCy - size * 0.07f, seatX, seatY)
        sr.line(seatX, seatY, handleX, handleY)
        sr.end()
    }

    fun drawExitIcon(sr: ShapeRenderer, cx: Float, cy: Float, size: Float, color: Color) {
        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(color)
        // Door frame
        sr.rect(cx - size * 0.35f, cy - size * 0.4f, size * 0.5f, size * 0.8f)
        // Arrow pointing out
        sr.line(cx - size * 0.1f, cy, cx + size * 0.45f, cy)
        sr.line(cx + size * 0.45f, cy, cx + size * 0.25f, cy + size * 0.2f)
        sr.line(cx + size * 0.45f, cy, cx + size * 0.25f, cy - size * 0.2f)
        sr.end()
    }

    fun drawSettingsGear(sr: ShapeRenderer, cx: Float, cy: Float, size: Float, color: Color) {
        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(color)
        circle(sr, cx, cy, size * 0.28f)
        circle(sr, cx, cy, size * 0.12f)
        val teeth = 8
        for (i in 0 until teeth) {
            val angle = (360f / teeth) * i
            val innerR = size * 0.3f
            val outerR = size * 0.42f
            val rad = angle * MathUtils.degreesToRadians
            val x1 = cx + MathUtils.cos(rad) * innerR
            val y1 = cy + MathUtils.sin(rad) * innerR
            val x2 = cx + MathUtils.cos(rad) * outerR
            val y2 = cy + MathUtils.sin(rad) * outerR
            sr.line(x1, y1, x2, y2)
        }
        sr.end()
    }

    fun drawBackArrow(sr: ShapeRenderer, cx: Float, cy: Float, size: Float, color: Color) {
        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(color)
        sr.line(cx + size * 0.3f, cy, cx - size * 0.3f, cy)
        sr.line(cx - size * 0.3f, cy, cx - size * 0.05f, cy + size * 0.25f)
        sr.line(cx - size * 0.3f, cy, cx - size * 0.05f, cy - size * 0.25f)
        sr.end()
    }

    fun drawCheck(sr: ShapeRenderer, cx: Float, cy: Float, size: Float, color: Color) {
        sr.begin(ShapeRenderer.ShapeType.Line)
        sr.setColor(color)
        sr.line(cx - size * 0.28f, cy, cx - size * 0.05f, cy - size * 0.22f)
        sr.line(cx - size * 0.05f, cy - size * 0.22f, cx + size * 0.3f, cy + size * 0.25f)
        sr.end()
    }

    private fun circle(sr: ShapeRenderer, cx: Float, cy: Float, r: Float, segments: Int = 24) {
        sr.circle(cx, cy, r, segments)
    }
}

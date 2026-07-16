package com.example.mbx

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable

/**
 * Shared, expensive-to-create rendering resources (batch, shape renderer, fonts).
 * Created once in [MountainBikeGame] and reused by every screen — screens must
 * NOT dispose these themselves.
 */
class Assets : Disposable {
    val batch = SpriteBatch()
    val shapeRenderer = ShapeRenderer()
    val layout = GlyphLayout()

    // Default LibGDX font (built-in bitmap, no external asset files needed).
    // Scaled up since the default font is quite small at native size.
    val titleFont = BitmapFont().apply { data.setScale(2.2f) }
    val bodyFont = BitmapFont().apply { data.setScale(1.5f) }
    val labelFont = BitmapFont().apply { data.setScale(1.8f) }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        titleFont.dispose()
        bodyFont.dispose()
        labelFont.dispose()
    }
}

package com.example.mbx.physics

import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable

/**
 * Box2D scaffold for the actual bike gameplay (NOT wired into any screen yet —
 * this is set up now so the next step, the physics gameplay screen, can build
 * directly on top of it).
 *
 * Bike model: one rectangular chassis body + two circular wheel bodies,
 * each wheel attached to the chassis with a RevoluteJoint (acts like a
 * simplified suspension-less wheel axle — swap for WheelJointDef later if
 * you want spring suspension).
 */
class BikePhysicsWorld : Disposable {

    val world = World(Vector2(0f, -18f), true)

    lateinit var chassis: Body
        private set
    lateinit var rearWheel: Body
        private set
    lateinit var frontWheel: Body
        private set

    fun spawnBike(x: Float, y: Float) {
        chassis = createChassis(x, y)
        rearWheel = createWheel(x - 0.55f, y - 0.35f)
        frontWheel = createWheel(x + 0.55f, y - 0.35f)

        attachWheel(chassis, rearWheel)
        attachWheel(chassis, frontWheel)
    }

    private fun createChassis(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val body = world.createBody(bodyDef)

        val shape = PolygonShape().apply {
            setAsBox(0.6f, 0.18f)
        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 1f
            friction = 0.5f
            restitution = 0.1f
        }
        body.createFixture(fixtureDef)
        shape.dispose()
        return body
    }

    private fun createWheel(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val body = world.createBody(bodyDef)

        val shape = CircleShape().apply { radius = 0.25f }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 1.2f
            friction = 0.9f
            restitution = 0.05f
        }
        body.createFixture(fixtureDef)
        shape.dispose()
        return body
    }

    private fun attachWheel(chassisBody: Body, wheelBody: Body) {
        val jointDef = RevoluteJointDef().apply {
            initialize(chassisBody, wheelBody, wheelBody.position)
            enableMotor = true
            maxMotorTorque = 8f
            motorSpeed = 0f
        }
        world.createJoint(jointDef)
    }

    /** Call once per frame with a fixed timestep (e.g. 1/60f) for stable simulation. */
    fun step(delta: Float) {
        world.step(delta, 6, 2)
    }

    override fun dispose() {
        world.dispose()
    }
}

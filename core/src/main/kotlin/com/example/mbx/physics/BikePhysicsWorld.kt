package com.example.mbx.physics

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.utils.Disposable

/**
 * Full Box2D bike simulation used by GameplayScreen: chassis + rider (torso+head,
 * lightly jointed so it can flop on a hard crash) + two motorised wheels.
 *
 * Units are meters (Box2D convention) — 1 world unit ≈ 1 meter. The rendering
 * camera in GameplayScreen uses the same coordinate system directly, no pixel
 * conversion needed.
 */
class BikePhysicsWorld : Disposable {

    companion object {
        const val WHEEL_RADIUS = 0.28f
        const val REAR_OFFSET_X = -0.55f
        const val FRONT_OFFSET_X = 0.55f
        const val WHEEL_OFFSET_Y = -0.30f
        const val MAX_PEDAL_TORQUE = 55f
        const val MAX_PEDAL_SPEED = 34f
        const val LEAN_TORQUE = 22f
        const val CRASH_ANGLE_DEG = 78f
    }

    val world = World(Vector2(0f, -22f), true)

    lateinit var chassis: Body
        private set
    lateinit var rearWheel: Body
        private set
    lateinit var frontWheel: Body
        private set
    lateinit var torso: Body
        private set
    lateinit var head: Body
        private set

    private lateinit var rearMotor: RevoluteJoint
    private lateinit var frontMotor: RevoluteJoint
    private lateinit var torsoJoint: RevoluteJoint

    private var spawnX = 0f
    private var spawnY = 0f

    /** True once the rider has separated from the bike (hard crash). */
    var crashed = false
        private set

    fun spawnBike(x: Float, y: Float) {
        spawnX = x
        spawnY = y

        chassis = createChassis(x, y)
        rearWheel = createWheel(x + REAR_OFFSET_X, y + WHEEL_OFFSET_Y)
        frontWheel = createWheel(x + FRONT_OFFSET_X, y + WHEEL_OFFSET_Y)
        rearMotor = attachWheel(chassis, rearWheel)
        frontMotor = attachWheel(chassis, frontWheel)

        torso = createTorso(x + 0.05f, y + 0.55f)
        head = createHead(x + 0.12f, y + 0.95f)

        val torsoJointDef = RevoluteJointDef().apply {
            initialize(chassis, torso, Vector2(x, y + 0.12f))
            enableLimit = true
            lowerAngle = -0.35f
            upperAngle = 0.35f
        }
        torsoJoint = world.createJoint(torsoJointDef) as RevoluteJoint

        val headJointDef = RevoluteJointDef().apply {
            initialize(torso, head, Vector2(x + 0.08f, y + 0.72f))
        }
        world.createJoint(headJointDef)

        crashed = false
    }

    /** Destroys all bodies/joints and rebuilds the bike at the original spawn point. */
    fun reset() {
        val bodies = com.badlogic.gdx.utils.Array<Body>()
        world.getBodies(bodies)
        for (b in bodies) world.destroyBody(b)
        spawnBike(spawnX, spawnY)
    }

    /** Positive = pedal forward, negative = reverse/brake-pedal, 0 = coast. */
    fun setPedalInput(direction: Float) {
        val target = -direction * MAX_PEDAL_SPEED // negative sign: forward roll = clockwise = negative angular vel in Box2D screen convention
        rearMotor.motorSpeed = target
        frontMotor.motorSpeed = target * 0.6f
        rearMotor.maxMotorTorque = MAX_PEDAL_TORQUE
        frontMotor.maxMotorTorque = MAX_PEDAL_TORQUE * 0.5f
    }

    /** Locks the wheels (high resistance) — used for the brake button. */
    fun applyBrake() {
        rearMotor.motorSpeed = 0f
        frontMotor.motorSpeed = 0f
        rearMotor.maxMotorTorque = MAX_PEDAL_TORQUE * 3f
        frontMotor.maxMotorTorque = MAX_PEDAL_TORQUE * 3f
    }

    /** Leans the chassis by applying a torque; positive = lean forward/clockwise. */
    fun lean(direction: Float) {
        chassis.applyTorque(direction * LEAN_TORQUE, true)
    }

    fun step(delta: Float) {
        world.step(delta, 8, 3)
        checkCrash()
    }

    private fun checkCrash() {
        if (crashed) return
        val angleDeg = MathUtils.radiansToDegrees * chassis.angle
        val normalized = ((angleDeg % 360f) + 360f) % 360f
        val deviation = if (normalized > 180f) 360f - normalized else normalized
        if (deviation > CRASH_ANGLE_DEG) {
            crashed = true
        }
    }

    private fun createChassis(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val body = world.createBody(bodyDef)
        val shape = PolygonShape().apply { setAsBox(0.62f, 0.16f) }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 1.1f
            friction = 0.4f
            restitution = 0.05f
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
        val shape = CircleShape().apply { radius = WHEEL_RADIUS }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 1.4f
            friction = 1.4f
            restitution = 0.05f
        }
        body.createFixture(fixtureDef)
        shape.dispose()
        return body
    }

    private fun createTorso(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val body = world.createBody(bodyDef)
        val shape = PolygonShape().apply { setAsBox(0.10f, 0.30f) }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 0.6f
            friction = 0.3f
            restitution = 0f
        }
        body.createFixture(fixtureDef)
        shape.dispose()
        return body
    }

    private fun createHead(x: Float, y: Float): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(x, y)
        }
        val body = world.createBody(bodyDef)
        val shape = CircleShape().apply { radius = 0.16f }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 0.5f
            friction = 0.3f
            restitution = 0.1f
        }
        body.createFixture(fixtureDef)
        shape.dispose()
        return body
    }

    private fun attachWheel(chassisBody: Body, wheelBody: Body): RevoluteJoint {
        val jointDef = RevoluteJointDef().apply {
            initialize(chassisBody, wheelBody, wheelBody.position)
            enableMotor = true
            maxMotorTorque = MAX_PEDAL_TORQUE
            motorSpeed = 0f
        }
        return world.createJoint(jointDef) as RevoluteJoint
    }

    override fun dispose() {
        world.dispose()
    }
}

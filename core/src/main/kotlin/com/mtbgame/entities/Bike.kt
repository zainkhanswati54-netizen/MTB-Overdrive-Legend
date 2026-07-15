package com.mtbgame.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef
import com.mtbgame.world.CATEGORY_BIKE
import com.mtbgame.world.CATEGORY_GROUND

/**
 * Physics-based bike: ek chassis (body) + do wheels (circles), WheelJoint se juda hua
 * (jaise real suspension). Rear wheel motor se drive hoti hai; player lean/flip ke liye
 * chassis par direct torque apply karta hai — isi se woh "backflip / forward flip" wala
 * trick-feel aata hai jo Trials-style bike games mein hota hai.
 */
class Bike(world: World, startX: Float, startY: Float) {

    val chassis: Body
    val frontWheel: Body
    val rearWheel: Body
    private val frontJoint: WheelJointDef
    private val rearJoint: WheelJointDef

    var frontWheelJointObj: com.badlogic.gdx.physics.box2d.joints.WheelJoint
    var rearWheelJointObj: com.badlogic.gdx.physics.box2d.joints.WheelJoint

    companion object {
        const val WHEEL_RADIUS = 0.35f
        const val WHEEL_BASE = 1.6f          // front-rear wheel distance
        const val MAX_MOTOR_TORQUE = 55f
        const val MAX_MOTOR_SPEED = 30f       // rad/s
        const val LEAN_TORQUE = 18f
    }

    init {
        // --- Chassis ---
        val chassisDef = BodyDef()
        chassisDef.type = BodyDef.BodyType.DynamicBody
        chassisDef.position.set(startX, startY + 0.9f)
        chassis = world.createBody(chassisDef)

        val chassisShape = PolygonShape()
        // Simple bike-body silhouette: ek lambi thin polygon (rider+frame ka approx hitbox)
        val bodyVertices = floatArrayOf(
            -0.75f, -0.15f,
            0.75f, -0.15f,
            0.85f, 0.35f,
            0.1f, 0.75f,
            -0.6f, 0.4f
        )
        chassisShape.set(bodyVertices)

        val chassisFixture = FixtureDef()
        chassisFixture.shape = chassisShape
        chassisFixture.density = 1.1f
        chassisFixture.friction = 0.3f
        chassisFixture.filter.categoryBits = CATEGORY_BIKE
        chassisFixture.filter.maskBits = CATEGORY_GROUND
        val chassisFix = chassis.createFixture(chassisFixture)
        chassisFix.userData = "chassis"
        chassisShape.dispose()

        // --- Wheels ---
        rearWheel = createWheel(world, startX - WHEEL_BASE / 2f, startY)
        frontWheel = createWheel(world, startX + WHEEL_BASE / 2f, startY)

        // --- Wheel joints (suspension + motor) ---
        rearJoint = WheelJointDef().apply {
            bodyA = chassis
            bodyB = rearWheel
            localAxisA.set(0f, 1f)
            localAnchorA.set(Vector2(rearWheel.position).sub(chassis.position))
            frequencyHz = 4.5f
            dampingRatio = 0.6f
            enableMotor = true
            maxMotorTorque = MAX_MOTOR_TORQUE
        }
        frontJoint = WheelJointDef().apply {
            bodyA = chassis
            bodyB = frontWheel
            localAxisA.set(0f, 1f)
            localAnchorA.set(Vector2(frontWheel.position).sub(chassis.position))
            frequencyHz = 4.5f
            dampingRatio = 0.6f
            enableMotor = false
            maxMotorTorque = 0f
        }

        rearWheelJointObj = world.createJoint(rearJoint) as com.badlogic.gdx.physics.box2d.joints.WheelJoint
        frontWheelJointObj = world.createJoint(frontJoint) as com.badlogic.gdx.physics.box2d.joints.WheelJoint
    }

    private fun createWheel(world: World, x: Float, y: Float): Body {
        val def = BodyDef()
        def.type = BodyDef.BodyType.DynamicBody
        def.position.set(x, y + 1.0f)
        val body = world.createBody(def)

        val shape = CircleShape()
        shape.radius = WHEEL_RADIUS

        val fixture = FixtureDef()
        fixture.shape = shape
        fixture.density = 1.3f
        fixture.friction = 1.4f
        fixture.restitution = 0.05f
        fixture.filter.categoryBits = CATEGORY_BIKE
        fixture.filter.maskBits = CATEGORY_GROUND
        val fix = body.createFixture(fixture)
        fix.userData = "wheel"
        shape.dispose()
        return body
    }

    /** Throttle: +1 = aage (forward drive), -1 = brake/reverse, 0 = neutral */
    fun setThrottle(direction: Float) {
        rearWheelJointObj.motorSpeed = -direction * MAX_MOTOR_SPEED
    }

    /** Lean back/forward for tricks and balance. direction: +1 = lean back, -1 = lean forward */
    fun applyLean(direction: Float) {
        chassis.applyTorque(direction * LEAN_TORQUE, true)
    }

    fun position(): Vector2 = chassis.position

    fun isCrashed(): Boolean {
        // Agar chassis body ka angle bahut tilt ho jaaye (upside down ke kareeb), crash maano
        val angleDeg = Math.toDegrees(chassis.angle.toDouble()).toFloat() % 360f
        val normalized = ((angleDeg + 360f) % 360f)
        return normalized in 100f..260f
    }
}

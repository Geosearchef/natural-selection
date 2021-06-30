package environment

import agent.Agent
import util.math.Vector

class Being(pos: Vector, val agent: Agent?, val healthLoss: Double = 0.3, val healthLowerBound: Double = 3.0) : Entity(pos) {
    var orientation: Double = Math.random() * Math.PI * 2.0
        set(value) {
            field = value % (Math.PI * 2.0)
        }
    var speed: Double = 20.0
    val direction get() = Vector(Math.sin(orientation), Math.cos(orientation))
    val velocity get() = direction * speed

    var health = 10.0

    override fun update(delta: Double, env: Environment): Boolean {
        this.pos += velocity * delta
        this.health -= healthLoss * delta

        return health > healthLowerBound
    }
}
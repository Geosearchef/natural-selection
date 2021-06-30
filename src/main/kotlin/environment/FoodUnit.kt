package environment

import util.math.Vector

class FoodUnit(pos: Vector, val amount: Double) : Entity(pos) {
    override fun update(delta: Double, env: Environment): Boolean {
        env.beings.find { (it.pos - this.pos).lengthSquared() < 15*15 }?.let { being ->
            being.health += this.amount
            return false
        }
        return true
    }
}
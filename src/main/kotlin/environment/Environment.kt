package environment

import util.math.Vector

class Environment(val size: Vector) {

    val entities = ArrayList<Entity>()
    val beings get() = entities.filterIsInstance<Being>()


    init {
        generateFood(100)
    }

    fun generateFood(count: Int) {
        repeat(count) {
            val pos = Vector(
                (10 until size.x.toInt() - 10).random().toDouble(),
                (10 until size.y.toInt() - 10).random().toDouble()
            )

            if(entities.filterIsInstance<FoodUnit>().none { (it.pos - pos).lengthSquared() < 10*10 }) {
                entities.add(FoodUnit(pos, 1.0))
            }
        }
    }
}
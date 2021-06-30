package environment

import util.math.Vector

class Environment(val size: Vector) {

    val entities = ArrayList<Entity>()
    val beings get() = entities.filterIsInstance<Being>()
    val foodUnits get() = entities.filterIsInstance<FoodUnit>()


    init {
        generateFood(100)
        generateBeings(50)
    }

    fun update(delta: Double) {
        val entitiesCopy = ArrayList(entities)
        entitiesCopy.forEach {
            val alive = it.update(delta, this)
            if(!alive) {
                this.entities.remove(it)
            }
        }
    }

    fun generateBeings(count: Int) {
        repeat(count) {
            val pos = randomPosition()

            if(beings.none { (it.pos - pos).lengthSquared() < 20*20 }) {
                entities.add(Being(pos, null))
            }
        }
    }

    fun generateFood(count: Int) {
        repeat(count) {
            val pos = randomPosition()

            if(foodUnits.none { (it.pos - pos).lengthSquared() < 10*10 }) {
                entities.add(FoodUnit(pos, 3.0))
            }
        }
    }

    fun randomPosition() = Vector(
        (10 until size.x.toInt() - 10).random().toDouble(),
        (10 until size.y.toInt() - 10).random().toDouble()
    )
}
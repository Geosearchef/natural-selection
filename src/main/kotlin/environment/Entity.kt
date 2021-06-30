package environment

import util.math.Vector

abstract class Entity(var pos: Vector) {
    abstract fun update(delta: Double, env: Environment): Boolean

}
//data class Position(val x: Int, val y: Int) {
//
//    fun wrap(): Position {
//        val wrappedX = if (x < 0) 19 else if (x >= 20) 0 else x
//        val wrappedY = if (y < 0) 15 else if (y >= 16) 0 else y
//        return Position(wrappedX, wrappedY)
//    }
//}

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) = Position(this.x + p.x, this.y + p.y)
    operator fun minus(p: Position) = Position(this.x - p.x, this.y - p.y)

    fun wrap(): Position {
        val wrappedX = (x + 20) % 20 // Garante que x esteja no intervalo [0, 19]
        val wrappedY = (y + 16) % 16 // Garante que y esteja no intervalo [0, 15]
        return Position(wrappedX, wrappedY)
    }
}

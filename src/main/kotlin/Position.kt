data class Position(val x: Int, val y: Int) {

    fun wrap(): Position {
        val wrappedX = if (x < 0) 19 else if (x >= 20) 0 else x
        val wrappedY = if (y < 0) 15 else if (y >= 16) 0 else y
        return Position(wrappedX, wrappedY)
    }
}
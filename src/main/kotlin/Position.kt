data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) = Position(this.x + p.x, this.y + p.y)
    operator fun minus(p: Position) = Position(this.x - p.x, this.y - p.y)

    // Define um wrap de x [0, 19] e y [0, 14]
    // A barra de estados ocupa uma celula em y que nao e ultrepassada
    fun wrap(): Position {
        val wrappedX = (x + 20) % 20 // Garante que x esteja no intervalo [0, 19]
        val wrappedY = (y + 15) % 15 // Garante que y esteja no intervalo [0, 14]
        return Position(wrappedX, wrappedY)
    }
}

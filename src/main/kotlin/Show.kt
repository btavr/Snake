import pt.isel.canvas.*

// Função de extensão para desenhar os tijolos
fun Canvas.drawBricks(wall: List<Position>) {
    for (brick in wall) {
        this.drawImage("brick.png", brick.x * CEL, brick.y * CEL, CEL, CEL)
    }
}

fun generateBrick(game: Game): List<Position> {
    val totalCells = (0 until 20).flatMap { x -> (0 until 16).map { y -> Position(x, y) } }
    val freeCells = totalCells.filter { it !in game.snake.body && it !in game.wall }

    return if (freeCells.isNotEmpty()) {
        game.wall + freeCells.random()
    } else game.wall
}
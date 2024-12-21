import pt.isel.canvas.*

// Função de extensão para desenhar os tijolos
fun Canvas.drawBricks(wall: List<Position>) {
    for (brick in wall) {
        this.drawImage("brick.png", brick.x * 32, brick.y * 32, 32, 32)
    }
}

fun generateBrick(game: Game): List<Position> {
    val totalCells = (0 until 20).flatMap { x -> (0 until 14).map { y -> Position(x, y) } }
    val freeCells = totalCells.filter {
        it !in game.snake.body && it !in game.wall && it !in game.dynamicWall
    }

    return if (freeCells.isNotEmpty()) {
        game.dynamicWall + freeCells.random()
    } else {
        game.dynamicWall // Não adiciona mais tijolos se não houver posições livres
    }
}

fun initialBricks(): List<Position> {
    return listOf(
        Position(1, 0), Position(2, 0), Position(0, 0), Position(0, 1), Position(0, 2), Position(0, 3),
        Position(19, 2), Position(19, 3),Position(17, 0), Position(18, 0),Position(19, 0), Position(19, 1),

        Position(0, 14), Position(1, 14), Position(2, 14), Position(0, 13), Position(0, 12), Position(0, 11),
        Position(17, 14), Position(18, 14),Position(19, 14), Position(19, 13), Position(19, 12), Position(19, 11),
    )
}

// Função para desenhar a maçã com base na posição atual do jogo
fun Canvas.drawApple(game: Game) {
    game.apple?.let { apple ->
        this.drawImage("snake|0,192,64,64", apple.x * 32, apple.y * 32, 32, 32)
    }
}

// Gera uma nova posição válida para a maçã
fun generateApple(game: Game): Position? {
    val totalCells = (0 until 20).flatMap { x -> (0 until 15).map { y -> Position(x, y) } }
    val freeCells = totalCells.filter {
        it !in game.snake.body && it !in game.wall && it !in game.dynamicWall
    }
    return if (freeCells.isNotEmpty()) freeCells.random() else null
}
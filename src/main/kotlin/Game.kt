import pt.isel.canvas.*

data class Game(val snake: Snake, val wall: List<Position>, val score: Int, val elapsedTime: Long)

fun main() {
    onStart {
        val arena = Canvas(640, 512, YELLOW) // 20x16 células de 32px
        val snake = Snake(listOf(Position(10, 8), Position(10 - 1, 8)), Direction.RIGHT)
        var game = Game(snake, wall = initialBricks(), 10, 0L)

        arena.onKeyPressed { key ->
            val oldDirection = game.snake.direction
            val newDirection = when (key.code) {
                UP_CODE -> Direction.UP
                DOWN_CODE -> Direction.DOWN
                LEFT_CODE -> Direction.LEFT
                RIGHT_CODE -> Direction.RIGHT
                else -> game.snake.direction
            }
            if (newDirection != game.snake.direction.opposite()) {
                val newSnakeDirection = game.snake.changeDirection(newDirection)
                if (newSnakeDirection.nextHeadPosition() in game.wall){
                    game.snake.changeDirection(oldDirection)
                } else{
                    game = game.copy(snake = newSnakeDirection)
                }
            }
        }

        arena.onTimeProgress(250) { elapsed ->
            val nextPosition = game.snake.nextHeadPosition()
            val newSnake = if (nextPosition in game.wall || nextPosition in game.snake.body) {
                game.snake // Cobra colidiu com um tijolo ou consigo mesma
            } else {
                game.snake.move()
            }

            val newWall = if (elapsed / 5000 > game.wall.size) {
                generateBrick(game)
            } else {
                game.wall
            }

            game = game.copy(
                snake = newSnake,
                wall = newWall,
                elapsedTime = elapsed // Atualiza o tempo decorrido
            )

            drawGame(arena, game)
        }
    }
    onFinish {}
}

fun drawGame(arena: Canvas, game: Game) {
    arena.erase()
    // Desenhar cobra
    for (segment in game.snake.body) {
        arena.drawSnake(game.snake)
    }
    // Desenhar tijolos
    arena.drawBricks(game.wall)
    //arena.drawLine(0,490, 640,490, GREEN, 50)

    arena.drawRect(0, 490, arena.width, 22, BLACK)

    val timeInSeconds = (game.elapsedTime / 1000).toInt()
    arena.drawText(10, 510, "Size: ${game.snake.body.size}", WHITE)
    arena.drawText(150, 510, "Score: ${game.score}", WHITE)
    arena.drawText(300, 510, "Time: $timeInSeconds s", WHITE)
}

fun initialBricks(): List<Position> {
    return listOf(
        Position(0, 0), Position(0, 1), Position(0, 2), Position(0, 3), // Cantos
        Position(19, 0), Position(19, 1), Position(19, 2), Position(19, 3),
        Position(1, 0), Position(2, 0),
        Position(18, 0), Position(17, 0)
    )
}



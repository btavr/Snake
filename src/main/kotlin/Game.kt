import pt.isel.canvas.*

data class Game(val snake: Snake, val wall: List<Position>, val apple: Position?, val score: Int, val elapsedTime: Long, val dynamicWall: List<Position>)

fun main() {
    onStart {
        val arena = Canvas(640, 512, YELLOW) // 20x16 células de 32px
        val snake = Snake(listOf(Position(10, 8), Position(10 - 1, 8)), Direction.RIGHT)
        var game = Game(snake, initialBricks(), apple = null, 0, 0L, dynamicWall = emptyList())

        game = game.copy(
            apple = generateApple(game)
        )
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

        arena.onTimeProgress(200) { elapsed ->
            val nextPosition = game.snake.nextHeadPosition()
            val newSnake = if (nextPosition in game.wall || nextPosition in game.dynamicWall || nextPosition in game.snake.body) {
                game.snake // Cobra colidiu com um tijolo ou consigo mesma
            } else {
                game.snake.move()
            }

            // Adicionar novos tijolos dinamicamente a cada 5000ms
            val newDynamicWall = if (elapsed / 5000 > game.dynamicWall.size) {
                generateBrick(game)
            } else {
                game.dynamicWall
            }
            var newScore = game.score
            val newApple = if (game.apple != null && nextPosition == game.apple) {
                 newScore += 1
                generateApple(game) // Gera nova posição para a maçã
            } else {
                game.apple // Mantém a posição atual da maçã
            }

            game = game.copy(
                snake = newSnake,
                dynamicWall = newDynamicWall,
                apple = newApple,
                score = newScore,
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
    arena.drawBricks(game.dynamicWall)
    arena.drawApple(game)

    arena.drawRect(0, 480, arena.width, 64, GREEN)

    val timeInSeconds = (game.elapsedTime / 1000).toInt()
    arena.drawText(10, 510, "Size: ${game.snake.body.size}", WHITE, 25)
    arena.drawText(150, 510, "Score: ${game.score}", WHITE,25)
    arena.drawText(300, 510, "Time: $timeInSeconds s", WHITE,25)
}

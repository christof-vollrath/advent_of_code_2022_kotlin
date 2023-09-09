import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

val exampleStrategyInput = """
A Y
B X
C Z
    """.trimIndent()

enum class GameShape(val score: Int) { ROCK(1), PAPER(2), SCISSOR(3) }
enum class GameResult { LOSE, DRAW, WIN }

fun findStrategy(result: GameResult, player1: GameShape) = when(result) {
    GameResult.LOSE -> when(player1) {
        GameShape.ROCK -> GameShape.SCISSOR
        GameShape.PAPER -> GameShape.ROCK
        GameShape.SCISSOR -> GameShape.PAPER
    }
    GameResult.DRAW -> player1
    GameResult.WIN -> when(player1) {
        GameShape.ROCK -> GameShape.PAPER
        GameShape.PAPER -> GameShape.SCISSOR
        GameShape.SCISSOR -> GameShape.ROCK
    }
}

fun parseStrategy(input: String): List<Pair<GameShape, GameShape>> = input.split("\n").map { line ->
    val (player1, player2) = line.split(" ").map {
        parseGameShape(it)
    }
    Pair(player1, player2)
}

fun parseGameShape(shapeString: String) = when (shapeString) {
    "A", "X" -> GameShape.ROCK
    "B", "Y" -> GameShape.PAPER
    "C", "Z" -> GameShape.SCISSOR
    else -> throw IllegalArgumentException()
}

fun parseGameResult(resultString: String) = when (resultString) {
    "X" -> GameResult.LOSE
    "Y" -> GameResult.DRAW
    "Z" -> GameResult.WIN
    else -> throw IllegalArgumentException()
}

fun parseStrategy2(input: String): List<Pair<GameShape, GameResult>> = input.split("\n").map {
    val (player1, player2) = it.split(" ")
    Pair(parseGameShape(player1), parseGameResult(player2))
}

fun calculateTotalStrategyScore(exampleStrategy: List<Pair<GameShape, GameShape>>) = exampleStrategy.sumOf {
    calculateOutcome(it.first, it.second)
} + exampleStrategy.sumOf { it.second.score }

fun calculateTotalStrategyScore2(exampleStrategy: List<Pair<GameShape, GameResult>>) = calculateTotalStrategyScore(exampleStrategy.map {
    Pair(it.first, findStrategy(it.second, it.first))
})

fun calculateOutcome(player: GameShape, you: GameShape) =
    if (player == you) 3
    else if ((player == GameShape.ROCK && you == GameShape.PAPER) ||
        (player == GameShape.PAPER && you == GameShape.SCISSOR) ||
        (player == GameShape.SCISSOR && you == GameShape.ROCK)) 6
    else 0

class Day02Part1: BehaviorSpec() { init {
    Given("a player shape and your shape") {
        val player = GameShape.ROCK
        val you = GameShape.PAPER
        Then("it should calculate outcome") {
            calculateOutcome(player, you) shouldBe 6
        }
    }
    Given("example input") {
        val exampleStrategy = parseStrategy(exampleStrategyInput)
        Then("it should have found 3 strategy lines") {
            exampleStrategy.size shouldBe 3
            exampleStrategy[0].first shouldBe GameShape.ROCK
            exampleStrategy[0].second shouldBe GameShape.PAPER
        }
        Then("it should calculate total strategy score") {
            val totalScore = calculateTotalStrategyScore(exampleStrategy)
            totalScore shouldBe 15
        }
    }
    Given("exercise input") {
        val exerciseStrategy = parseStrategy(readResource("inputDay02.txt")!!)
        val totalScore = calculateTotalStrategyScore(exerciseStrategy)
        Then("it should have calculated a total score of 13.446") {
            totalScore shouldBe 13_446
        }
    }
} }

class Day02Part2: BehaviorSpec() { init {
    Given("a way to find the correct shape") {
        val shape = findStrategy(GameResult.DRAW, GameShape.PAPER)
        Then("it should found the strategy") {
            shape shouldBe GameShape.PAPER
        }
    }
    Given("example input") {
        val exampleStrategy = parseStrategy2(exampleStrategyInput)
        Then("it should have found 3 strategy lines") {
            exampleStrategy.size shouldBe 3
            exampleStrategy[0].first shouldBe GameShape.ROCK
            exampleStrategy[0].second shouldBe GameResult.DRAW
        }
        Then("it should calculate total strategy score") {
            val totalScore = calculateTotalStrategyScore2(exampleStrategy)
            totalScore shouldBe 12
        }
    }
    Given("exercise input") {
        val exerciseStrategy = parseStrategy2(readResource("inputDay02.txt")!!)
        val totalScore = calculateTotalStrategyScore2(exerciseStrategy)
        Then("it should have calculated a total score of 13.509") {
            totalScore shouldBe 13_509
        }
    }
} }


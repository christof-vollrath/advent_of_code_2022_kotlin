import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.math.abs

val movementsInput = """
    R 4
    U 4
    L 3
    D 1
    R 4
    D 1
    L 5
    R 2
    """.trimIndent()
class Day09Part1: BehaviorSpec() { init {
    Given("move input") {
        When("parsing input") {
            val movements = parseMovements(movementsInput)
            Then("it should have parsed 8 movements") {
                movements.size shouldBe 8
            }
            Then("parsed movements should be right") {
                movements[0].shouldBeInstanceOf<MoveRight>()
                movements[0].steps shouldBe 4
                movements[1].shouldBeInstanceOf<MoveUp>()
                movements[1].steps shouldBe 4
                movements[2].shouldBeInstanceOf<MoveLeft>()
                movements[2].steps shouldBe 3
                movements[5].shouldBeInstanceOf<MoveDown>()
                movements[5].steps shouldBe 1
            }
            When("executing one move") {
                val result = executeMovements(movements.take(1), List(2) { Coord2(0,0) })
                Then("head should have moved and tail too") {
                    result.knots[0] shouldBe Coord2(4, 0)
                    result.knots[1] shouldBe Coord2(3, 0)
                    result.tailTrail.size shouldBe 4
                }
            }
            When("executing two moves") {
                val result = executeMovements(movements.take(2), List(2) { Coord2(0,0) })
                Then("head should have moved and tail too") {
                    result.knots[0] shouldBe Coord2(4, -4)
                    result.knots[1] shouldBe Coord2(4, -3)
                    result.tailTrail.toSet().size shouldBe 7
                }
            }
            When("executing five moves") {
                val result = executeMovements(movements.take(5), List(2) { Coord2(0,0) })
                Then("head should have moved and tail too") {
                    result.knots[0] shouldBe Coord2(5, -3)
                    result.knots[1] shouldBe Coord2(4, -3)
                }
            }
            When("executing all movements") {
                val result = executeMovements(movements, List(2) { Coord2(0,0) })
                Then("head should have moved and trail followed") {
                    result.knots[0] shouldBe Coord2(2, -2)
                    result.knots[1] shouldBe Coord2(1, -2)
                    result.tailTrail.toSet().size shouldBe 13
                }
            }
        }
    }
    Given("exercise input") {
        val exerciseInput = readResource("inputDay09.txt")!!
        When("parsing input and executing movements") {
            val movements = parseMovements(exerciseInput)
            val result = executeMovements(movements, List(2) { Coord2(0,0) })
            Then("head should have moved and trail followed") {
                result.tailTrail.toSet().size shouldBe 6044
            }

        }
    }
} }

class Day09Part2: BehaviorSpec() { init {
    Given("parsed move input") {
        val movements = parseMovements(movementsInput)
        When("executing all moves") {
            val result = executeMovements(movements, List(10) { Coord2(0,0) })
            Then("head should have moved and trail followed") {
                result.knots[0] shouldBe Coord2(2, -2)
                result.knots[1] shouldBe Coord2(1, -2)
                result.knots[2] shouldBe Coord2(2, -2)
                result.knots[3] shouldBe Coord2(3, -2)
                result.knots[5] shouldBe Coord2(1, -1)
                result.knots[6] shouldBe Coord2(0, 0)
                result.tailTrail.toSet().size shouldBe 1 // not yet moved
            }
        }
    }
    Given("A larger example") {
        val movements = parseMovements("""
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
        """.trimIndent())
        When("executing all moves") {
            val result = executeMovements(movements, List(10) { Coord2(0,0) })
            Then("head should have moved and trail followed") {
                result.knots[0] shouldBe Coord2(-11, -15)
                result.knots[9] shouldBe Coord2(-11, -6)
                result.tailTrail.toSet().size shouldBe 36
            }
        }
    }
    Given("exercise input") {
        val exerciseInput = readResource("inputDay09.txt")!!
        When("parsing input and executing movements") {
            val movements = parseMovements(exerciseInput)
            val result = executeMovements(movements, List(10) { Coord2(0,0) })
            Then("head should have moved and trail followed") {
                result.tailTrail.toSet().size shouldBe 2384
            }
        }
    }
} }

private fun parseMovements(movementsInput: String): List<Movement> = movementsInput.split("\n").map { line ->
    val parts = line.split(" ").map { it.trim() }
    val steps = parts[1].toInt()
    when (parts[0]) {
        "R" -> MoveRight(steps)
        "L" -> MoveLeft(steps)
        "U" -> MoveUp(steps)
        "D" -> MoveDown(steps)
        else -> throw IllegalArgumentException("Unexpected move ${parts[0]}")
    }
}
sealed class Movement(open val steps: Int, val singleMove: Coord2)
data class MoveRight(override val steps: Int) : Movement(steps, Coord2(1, 0))
data class MoveLeft(override val steps: Int) : Movement(steps, Coord2(-1, 0))
data class MoveUp(override val steps: Int) : Movement(steps, Coord2(0, -1))
data class MoveDown(override val steps: Int) : Movement(steps, Coord2(0, 1))


private fun executeMovements(movements: List<Movement>, knots: List<Coord2>): MoveResult {
    var currentKnots = knots
    val tailTrail = mutableSetOf<Coord2>()
    for(movement in movements)
        for (s in 0 until movement.steps) {
            var currentPrev: Coord2? = null
            val nextKnots = mutableListOf<Coord2>()
            for ((i, knot) in currentKnots.withIndex()) {
                if (currentPrev == null) {
                    currentPrev = knot + movement.singleMove
                    nextKnots.add(currentPrev)
                } else {
                    val movedKnot = moveNextKnot(currentPrev, knot)
                    nextKnots.add(movedKnot)
                    if (i == knots.size - 1) tailTrail.add(movedKnot)
                    currentPrev = movedKnot
                }
            }
            currentKnots = nextKnots
        }
    return MoveResult(currentKnots, tailTrail)
}

fun moveNextKnot(prevKnot: Coord2, nextKnot: Coord2): Coord2 {
    fun norm (x: Int) = when {
        x > 0 -> 1
        x < 0 -> -1
        else -> 0
    }
    val diff = prevKnot - nextKnot
    return if (abs(diff.x) >= 2 || abs(diff.y) >= 2) { // move only when not touching
        val delta = Coord2(norm(diff.x), norm(diff.y))
        nextKnot + delta
    } else nextKnot
}

data class MoveResult(val knots: List<Coord2>, val tailTrail: Set<Coord2>)

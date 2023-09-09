import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

val exampleInput = """
    1000
    2000
    3000
    
    4000
    
    5000
    6000
    
    7000
    8000
    9000
    
    10000
    """.trimIndent()

fun parseItemsCalories(input: String): List<List<Int>> = input.split("\n").split { it.isEmpty() }.map { line -> line.map { it.toInt() }}

fun findMostCalories(data: List<List<Int>>): Int? = data.maxOfOrNull {
    it.sum()
}

fun findMost3Calories(data: List<List<Int>>): Int = data.map {
    it.sum()
}.sortedDescending().take(3).sum()

private fun <E> List<E>.split(predicate: (E) -> Boolean): List<List<E>> = sequence<List<E>> {
    var currentList = mutableListOf<E>()
    this@split.forEach {
        if (predicate(it)) {
            yield(currentList)
            currentList = mutableListOf()
        } else {
            currentList.add(it)
        }
    }
    yield(currentList)
}.toList()

class Day01ListSplit: FunSpec() { init{
    withData(
        Pair(emptyList(), listOf(emptyList())),
        Pair(listOf("a", "b"), listOf(listOf("a", "b"))),
        Pair(listOf("a", "b", "", "c"), listOf(listOf("a", "b"), listOf("c"))),
        Pair(listOf("a", "b", "", "c", "", "d", "e"), listOf(listOf("a", "b"), listOf("c"), listOf("d", "e"))),
    ) { (input, expected) ->
        input.split { it.isEmpty() } shouldBe expected
    }

} }

class Day01Part1: BehaviorSpec() { init {
    Given("example input") {
        val exampleData = parseItemsCalories(exampleInput)
        Then("it should have found 5 elves with items") {
            exampleData.size shouldBe 5
        }
        When("searching for the elves with the most calories") {
            val mostCalories = findMostCalories(exampleData)
            Then("it should have found 24.000 calories") {
                mostCalories shouldBe 24_000
            }
        }
    }
    Given("exercise input") {
        val exerciseData = parseItemsCalories(readResource("inputDay01.txt")!!)
        val mostCalories = findMostCalories(exerciseData)
        Then("it should have found 67_658 calories") {
            mostCalories shouldBe 67_658
        }
    }
} }
class Day01Part2: BehaviorSpec() { init {
    Given("example input") {
        val exampleData = parseItemsCalories(exampleInput)
        When("searching for the three elves with the most calories") {
            val mostCalories = findMost3Calories(exampleData)
            Then("it should have found 24.000 calories") {
                mostCalories shouldBe 45_000
            }
        }
    }
    Given("exercise input") {
        val exerciseData = parseItemsCalories(readResource("inputDay01.txt")!!)
        val mostCalories = findMost3Calories(exerciseData)
        Then("it should have found 200_158 calories") {
            mostCalories shouldBe 200_158
        }
    }
} }
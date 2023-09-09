import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

val exampleRucksacks = """
    vJrwpWtwJgWrhcsFMMfFFhFp
    jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
    PmmdzqPrVvPwwTWBwg
    wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
    ttgJtRGJQctTZtZT
    CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()

fun List<List<String>>.sumPrioritiesSharedItems(): Int =
    sumOf { it.sharedItems().priority() }

private fun List<String>.sharedItems() = reduce { a, b ->
    a.toList().intersect(b.toSet()).joinToString()
}

fun parseRucksacks(input: String): List<List<String>> = input.split("\n").map {
    val splitPosition = it.length / 2
    listOf(it.substring(0, splitPosition), it.substring(splitPosition))
}

fun parseRucksacks3(input: String): List<List<String>> = input.split("\n").chunked(3) {
    listOf(it[0], it[1], it[2])
}

private fun String.priority() =
    sumOf {
        when (it) {
            in 'a'..'z' -> it.code - 'a'.code + 1
            in 'A'..'Z' -> it.code - 'A'.code + 27
            else -> throw IllegalArgumentException("Unexpected char $it")
        }
    }

class Day03Priority: FunSpec() { init{
    withData(
        Pair("a", 1),
        Pair("c", 3),
        Pair("z", 26),
        Pair("A", 27),
        Pair("Z", 52),
        Pair("abX", 53),
    ) { (input, expected) ->
        input.priority() shouldBe expected
    }
} }

class Day03Part1: BehaviorSpec() { init {
    val exampleRucksacks = parseRucksacks(exampleRucksacks)
    Given("example input") {
        Then("it should have found 6 rucksacks") {
            exampleRucksacks.size shouldBe 6
            exampleRucksacks[0] shouldBe listOf("vJrwpWtwJgWr", "hcsFMMfFFhFp")
        }
    }
    Given("a pair of rucksacks") {
        val pairRucksacks = exampleRucksacks[0]
        Then("it should find shared items") {
            pairRucksacks.sharedItems() shouldBe "p"
        }
    }
    Given("example rucksacks") {
        Then("it should find sum of priorities of shared items") {
            exampleRucksacks.sumPrioritiesSharedItems() shouldBe 157
        }
    }
    Given("exercise input") {
        val exerciseRucksacks = parseRucksacks(readResource("inputDay03.txt")!!)
        val totalPriorities = exerciseRucksacks.sumPrioritiesSharedItems()
        Then("it should have calculated a priority of 7872") {
            totalPriorities shouldBe 7872
        }
    }
} }

class Day03Part2: BehaviorSpec() { init {
    val exampleRucksacks3 = parseRucksacks3(exampleRucksacks)
    Given("example input") {
        Then("it should have found 2 rucksacks for each line") {
            exampleRucksacks3.size shouldBe 2
            exampleRucksacks3[0] shouldBe listOf("vJrwpWtwJgWrhcsFMMfFFhFp", "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL", "PmmdzqPrVvPwwTWBwg")
        }
    }
    Given("example rucksacks") {
        Then("it should find sum of priorities of shared items") {
            exampleRucksacks3.sumPrioritiesSharedItems() shouldBe 70
        }
    }
    Given("exercise input") {
        val exerciseRucksacks3 = parseRucksacks3(readResource("inputDay03.txt")!!)
        val totalPriorities = exerciseRucksacks3.sumPrioritiesSharedItems()
        Then("it should have calculated a priority of 2497") {
            totalPriorities shouldBe 2497
        }
    }} }
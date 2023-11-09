import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class FindMFirstMarkerTest4: FunSpec() { init{
    withData(
        Pair("bvwbjplbgvbhsrlpgdmjqwftvncz", 5),
        Pair("nppdvjthqldpwncqszvftbrmjlhg", 6),
        Pair("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10),
        Pair("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11),
    ) { (str, expected) ->
        findFirstMarker(str, 4) shouldBe expected
    }
} }

class FindMFirstMarkerTest14: FunSpec() { init{
    withData(
        Pair("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 19),
        Pair("bvwbjplbgvbhsrlpgdmjqwftvncz", 23),
        Pair("nppdvjthqldpwncqszvftbrmjlhg", 23),
        Pair("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 29),
        Pair("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 26),
    ) { (str, expected) ->
        findFirstMarker(str, 14) shouldBe expected
    }
} }

class Day06Part1: BehaviorSpec() { init {
    Given("a string containing duplicates") {
        val stringWithDuplicates = "mjqj"
        Then("it should find duplicates") {
            hasDuplicates(stringWithDuplicates) shouldBe true
        }
    }
    Given("a string without duplicates") {
        val stringWithoutDuplicates = "vwbj"
        Then("it should find no duplicates") {
            hasDuplicates(stringWithoutDuplicates) shouldBe false
        }
    }
    Given("a long string with duplicates") {
        val stringWithDuplicates = "bvwbjplbgvbhsrlpgdmjqwftvncz"
        Then("it should find the first marker") {
            findFirstMarker(stringWithDuplicates, 4) shouldBe 5
        }

    }
    Given("exercise input") {
        val str = readResource("inputDay06.txt")!!
        Then("find first marker") {
            findFirstMarker(str, 4) shouldBe 1760
        }
    }
} }
class Day06Part2: BehaviorSpec() { init {
    Given("exercise input") {
        val str = readResource("inputDay06.txt")!!
        Then("find first marker") {
            findFirstMarker(str, 14) shouldBe 2974
        }
    }
} }

private fun findFirstMarker(str: String, len: Int): Int {
    for (i in 0 until str.length - len) {
        val subStr = str.substring(i until i + len)
        if (!hasDuplicates(subStr)) return i + len
    }
    throw IllegalArgumentException("Input has no marker")
}

private fun hasDuplicates(str: String): Boolean {
    val chars = mutableSetOf<Char>()
    for (c in str) {
        if (c in chars) return true
        chars.add(c)
    }
    return false
}


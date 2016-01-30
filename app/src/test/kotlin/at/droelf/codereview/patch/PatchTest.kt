package at.droelf.codereview.patch;

import android.text.SpannableString
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class PatchTest {

    var parser: PatchParser = PatchParser()

    @Before
    fun setup(): Unit{
        parser = PatchParser()
    }

    @Test
    @Throws(Exception::class)
    fun test_ParseLineType(){
        val sampleLines = mapOf(
                "- abc" to Patch.Type.Delete,
                "+ asdf" to Patch.Type.Add,
                "    - asd" to Patch.Type.Neutral,
                "  +" to Patch.Type.Neutral,
                "" to Patch.Type.Neutral,
                " " to Patch.Type.Neutral,
                "\n" to Patch.Type.Neutral,
                "123123123" to Patch.Type.Neutral
        )

        sampleLines.forEach { it ->
            assertThat(
                    "'${it.key}' should be parsed into: '${it.value}'",
                    parser.parseLineType(it.key),
                    `is`(it.value)
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun test_IsHeader(){
        val sampleHeaders = mapOf(
                "" to false,
                "@@ -31,7 +31,7 @@ public static void main(String[] args) throws Exception {" to true,
                "@@ -31,7 +31,7 @@" to true,
                "@@ -1,1 +1,1 @@" to true,
                " @@ -31,7 +31,7 @@ public static void main(String[] args) throws Exception {" to false,
                "@ -31,7 +31,7 @@ public static void main(String[] args) throws Exception {" to false,
                "@ -31,7 +31,7 @@ public static void main(String[] args) throws Exception {" to false,
                "@@ -31,7 +31,7 " to false,
                "@@ -31,7 @@" to false,
                "@ -31,7 +31,7 @" to false,
                "@@ -317 +317 @@" to false,
                "@@" to false,
                "@@ +31,7 -31,7 @@" to false,
                "@@ -31,7+31,7 @@" to false
        )

        sampleHeaders.forEach { i ->
            assertThat(
                    "${i.key} isHeader: ${i.value}",
                    parser.isHeader(i.key),
                    `is`(i.value)
            )
        }
    }


    @Test
    @Throws(Exception::class)
    fun test_ParseRange(){
        val sampleRanges = mapOf(
                "-31,7" to Patch.Range(31, 7),
                "+31,7" to Patch.Range(31, 7),
                "+3,7" to Patch.Range(3, 7),
                "+" to null,
                "31,7" to null,
                "+,7" to null,
                "+31," to null,
                " +31," to null,
                "+31,123,12" to null,
                "+31,,12" to null
        )

        sampleRanges.forEach { i ->
            assertThat(
                    "${i.key} isHeader: ${i.value}",
                    parser.parseRange(i.key),
                    `is`(i.value)
            )
        }
    }


    @Test
    @Throws(Exception::class)
    fun test_ParseHeader(){
        val sampleHeader = mapOf(
                "@@ -31,7 +31,8 @@" to Pair(Patch.Range(31,7),Patch.Range(31,8)),
                "@@ -22,7 +3,8 @@" to Pair(Patch.Range(22,7),Patch.Range(3,8)),
                "@@ -31,7 +31,7 @@ public static void main(String[] args) throws Exception {" to null
        )

        sampleHeader.forEach { i ->
            assertThat(
                    "${i.key} parseHeader: ${i.value}",
                    parser.parseHeader(i.key),
                    `is`(i.value)
            )
        }
    }

    fun mockSpannable(string: String){
        val sp = Mockito.mock(SpannableString::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun test_ParseCodeLines() {
        val testList = listOf(
                "+ test",
                "- test",
                "+ test",
                "test",
                "- test",
                "+ test",
                "+ test",
                "- test",
                "- test",
                "- test",
                "  test",
                "- test",
                "test",
                "  test",
                "- test"
        )
        val range = Pair(Patch.Range(12, 10), Patch.Range(15, 7))

        val test = listOf(
                Patch.Line("+ test", Patch.Type.Add, null, 15),
                Patch.Line("- test", Patch.Type.Delete, 12, null),
                Patch.Line("+ test", Patch.Type.Add, null, 16),
                Patch.Line("test", Patch.Type.Neutral, 13, 17),
                Patch.Line("- test", Patch.Type.Delete, 14, null),
                Patch.Line("+ test", Patch.Type.Add, null, 18),
                Patch.Line("+ test", Patch.Type.Add, null, 19),
                Patch.Line("- test", Patch.Type.Delete, 15, null),
                Patch.Line("- test", Patch.Type.Delete, 16, null),
                Patch.Line("- test", Patch.Type.Delete, 17, null),
                Patch.Line("  test", Patch.Type.Neutral, 18, 20),
                Patch.Line("- test", Patch.Type.Delete, 19, null),
                Patch.Line("test", Patch.Type.Neutral, 20, 21),
                Patch.Line("  test", Patch.Type.Neutral, 21, 22),
                Patch.Line("- test", Patch.Type.Delete, 22, null)
        )

        val actual = parser.parseCodeLines(testList, range)
        assertThat("Same size", actual.size, `is`(test.size))
        val trueCount = test.mapIndexed { i, line -> line.equals(actual.get(i)) }.count { it }
        assertThat("Equal", trueCount, `is`(test.size))

        assertThat(
                "equal",
                actual,
                `is`(test)
        )
    }

}
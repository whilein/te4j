/*
 *    Copyright 2021 Lero4ka16
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.github.lero4ka16.te4j.Te4j
import com.github.lero4ka16.te4j.Te4j.Companion.custom
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.util.Utils.Companion.deleteDirectory
import com.github.lero4ka16.te4j.util.type.ref.ClassRef
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import com.github.lero4ka16.te4j.util.type.ref.TypeRef
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * @author lero4ka16
 */
internal class TemplateTest {
    private lateinit var dummy: Any
    private lateinit var context: TemplateContext
    private lateinit var trimContext: TemplateContext
    private lateinit var hotReloadContext: TemplateContext

    private var tests: File? = null

    @AfterEach
    fun clean() {
        deleteDirectory(tests!!)
    }

    @BeforeEach
    fun init() {
        dummy = Any()
        context = custom()
            .useResources()
            .replace(Te4j.DEL_ALL)
            .build()
        trimContext = custom()
            .useResources()
            .build()
        hotReloadContext = custom()
            .replace(Te4j.DEL_ALL)
            .enableHotReloading(ModifyWatcherManager())
            .build()
        tests = File("tests")
        tests!!.mkdirs()
    }

    @Test
    @Throws(InterruptedException::class)
    fun testHotReloadConcurrency() {
        val path = Paths.get("tests/concurrency.txt")
        copyResource("WEB-INF/greeting.txt", path)
        Thread.sleep(100)

        val template = hotReloadContext.load(Pojo1::class.java, "tests/concurrency.txt")
        val expect = AtomicReference("Hello")

        val root = Thread {
            val threads = arrayOfNulls<Thread>(16)
            for (i in 0..15) {
                val thread = Thread {
                    val value = expect.get()
                    assertEquals(
                        "$value my girlfriend!",
                        template.renderAsString(Pojo1("my girlfriend"))
                    )
                }
                thread.start()
                threads[i] = thread
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            for (thread in threads) {
                try {
                    thread!!.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        root.start()
        copyResource("WEB-INF/farewell.txt", path)
        Thread.sleep(10)
        expect.set("Goodbye")
        root.join()
    }

    @Test
    fun testConcurrency() {
        val nThreads = Runtime.getRuntime().availableProcessors() * 2
        val threads = arrayOfNulls<Thread>(nThreads)
        for (i in 0 until nThreads) {
            val thread = Thread {
                try {
                    testTemplate(
                        context,
                        "WEB-INF/greeting.txt",
                        "Hello my friend!",
                        Pojo1("my friend"),
                        ClassRef(Pojo1::class.java)
                    )
                } catch (cause: Throwable) {
                    fail<Any>(cause)
                }
            }
            thread.start()
            threads[i] = thread
        }
        for (thread in threads) {
            try {
                thread!!.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun testTrim() {
        val expect: String = if (System.getProperty("os.name").contains("Windows")) {
            "Inline if: Yes\r\n\r\nMultiline list:\r\n  - ^Hi\r\n  - ^Hello\r\n  - ^Bye\r\n  - ^Goodbye\r\n"
        } else {
            "Inline if: Yes\n\nMultiline list:\n  - ^Hi\n  - ^Hello\n  - ^Bye\n  - ^Goodbye\n"
        }

        testTemplate(
            trimContext,
            "WEB-INF/trim.txt", expect,
            Pojo6(true, listOf("Hi", "Hello", "Bye", "Goodbye")),
            ClassRef(Pojo6::class.java)
        )
    }

    @Test
    @Throws(InterruptedException::class)
    fun testHotReload() {
        val plain1 = Paths.get("tests/hotreload_plain_1.txt")
        val plain2 = Paths.get("tests/hotreload_plain_2.txt")

        copyResource("WEB-INF/hotreload_plain_1.txt", plain1)
        copyResource("WEB-INF/hotreload_plain_1.txt", plain2)

        Thread.sleep(100)

        val template1: Template<Any> = hotReloadContext.loadFile(Any::class.java, plain1)
        val template2: Template<Any> = hotReloadContext.loadFile(Any::class.java, plain2)

        assertNotEquals("GeneratedTemplate", template1.javaClass.name)
        assertNotEquals("GeneratedTemplate", template2.javaClass.name)

        assertEquals("Before hot reload", template1.renderAsString(dummy))
        assertEquals("Before hot reload", template2.renderAsString(dummy))

        println("Before modify")

        copyResource("WEB-INF/hotreload_plain_2.txt", plain1)
        copyResource("WEB-INF/hotreload_plain_2.txt", plain2)

        println("After modify wait")

        // Слушание событий происходит в отдельном потоке
        // ждём немного, перед тем, чтобы сделать проверку
        Thread.sleep(100)

        println("Check")

        assertEquals("After hot reload", template1.renderAsString(dummy))
        assertEquals("After hot reload", template2.renderAsString(dummy))
    }

    @Test
    fun testPlain() {
        testTemplate(
            context,
            "WEB-INF/plain.txt", "Hello world!Привет мир!",
            dummy, ClassRef(Any::class.java)
        )
    }

    @Test
    fun testValue() {
        testTemplate(
            context,
            "WEB-INF/greeting.txt", "Hello my friend!",
            Pojo1("my friend"), ClassRef(Pojo1::class.java)
        )
    }

    @Test
    fun testForeach() {
        testTemplate(
            context,
            "WEB-INF/foreach.txt", "<a>0: 10</a><a>1: 20</a><a>2: 30</a><a>10</a><a>20</a><a>30</a>",
            Pojo2(10, 20, 30), ClassRef(Pojo2::class.java)
        )
    }

    @Test
    fun testForeachCollection() {
        testTemplate(
            context,
            "WEB-INF/foreach.txt", "<a>0: 15</a><a>1: 25</a><a>2: 35</a><a>15</a><a>25</a><a>35</a>",
            Pojo5(15, 25, 35), ClassRef(Pojo5::class.java)
        )
    }

    @Test
    fun testForeachGeneric() {
        testTemplate(context,
            "WEB-INF/foreach_generic.txt", "<a>0: 50</a><a>1: 100</a><a>2: 200</a>",
            listOf(50, 100, 200), object : TypeRef<List<Int>>() {})
    }

    @Test
    fun testConditionFalse() {
        testTemplate(
            context,
            "WEB-INF/condition.txt", "<a>Result is false</a>",
            Pojo3("Hello world", false), ClassRef(Pojo3::class.java)
        )
    }

    @Test
    fun testConditionTrue() {
        testTemplate(
            context,
            "WEB-INF/condition.txt", "<a>Hello world</a>",
            Pojo3("Hello world", true), ClassRef(Pojo3::class.java)
        )
    }

    @Test
    fun testSwitchCase_Condition() {
        testTemplate(
            context,
            "WEB-INF/switchcase.txt", "<a>Goodbye my friend</a>",
            Pojo4("Goodbye my friend", true), ClassRef(Pojo4::class.java)
        )
    }

    @Test
    fun testSwitchCase_Foreach() {
        testTemplate(
            context,
            "WEB-INF/switchcase.txt", "<a>0: 5</a><a>1: 10</a><a>2: 15</a><a>5</a><a>10</a><a>15</a>",
            Pojo4(intArrayOf(5, 10, 15)), ClassRef(Pojo4::class.java)
        )
    }

    @Test
    fun testSwitchCase_Value() {
        testTemplate(
            context,
            "WEB-INF/switchcase.txt", "Hello you!",
            Pojo4("you"), ClassRef(Pojo4::class.java)
        )
    }

    private fun copyResource(resource: String, to: Path) {
        try {
            ClassLoader.getSystemResourceAsStream(resource).use {
                Files.newOutputStream(to).use { os ->
                    assert(it != null)
                    val buf = ByteArray(1024)
                    var n: Int
                    while (it.read(buf).also { n = it } != -1) {
                        os.write(buf, 0, n)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun <T> testTemplate(
        context: TemplateContext, resource: String, expectText: String,
        `object`: T, type: ITypeRef<T>
    ) {
        val result = context.load(type, resource).renderAsString(`object`)
        assertEquals(expectText, result)
    }

    class Pojo1(val name: String)
    class Pojo2(vararg val elements: Int?)
    class Pojo3(val message: String, val condition: Boolean)

    class Pojo4(
        val check: Check,
        val name: String?,
        val elements: IntArray?,
        val message: String?,
        val isCondition: Boolean
    ) {
        constructor(name: String?) : this(Check.GREETING, name, null, null, false)
        constructor(elements: IntArray?) : this(Check.FOREACH, null, elements, null, false)
        constructor(message: String?, condition: Boolean) : this(Check.CONDITION, null, null, message, condition)
    }

    class Pojo5(vararg elements: Int) {
        private val elements: List<Int?> = IntStream.of(*elements).boxed().collect(Collectors.toList())

        fun getElements(): Collection<Int?> {
            return elements
        }

    }

    class Pojo6(val isCondition: Boolean, val list: List<String>)

    enum class Check {
        CONDITION, FOREACH, GREETING;

        val path = name.toLowerCase()
    }
}
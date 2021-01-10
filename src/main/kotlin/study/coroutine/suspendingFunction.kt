package study.coroutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class suspendingFunction {
    /*
    * 코루틴은 기본적으로 순차적으로 실행 됨
    * coroutine, just like in the regular code, is sequential bt default
    */
    fun test() {
        runBlocking {
            val time = measureTimeMillis {
                val one = doSomethingUseFulOne()
                val two = doSomethingUseFulTwo()
                println("The answer is ${one + two}")
            }
            println("Completed in $time ms")
        }
    }

    /*
    * async 예제
    * suspend가 종료될 때까지 기다리는 것이 아니라 실행 후 바로 넘어간다.
    * await(): async가 끝날때까지 기다린다.
    * async는 job(Defferd)를 리턴함
    */
    fun test1() {
        runBlocking {
            val time = measureTimeMillis {
                /* 실행 후 바로 넘어가기 때문에 거의 동시에 실행됨 */
                val one = async { doSomethingUseFulOne() }
                val two = async { doSomethingUseFulTwo() }
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    fun test2() {
        runBlocking {
            val time = measureTimeMillis {
                /*
                * start = CoroutineStart.LAZY
                * start()를 하거나 await()를 할 때 스타트한다.
                */
                val one = async(start = CoroutineStart.LAZY) { doSomethingUseFulOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingUseFulTwo() }

                one.start()
                two.start()

                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    suspend fun doSomethingUseFulOne(): Int {
        delay(1300L)
        return 13
    }

    suspend fun doSomethingUseFulTwo(): Int {
        delay(10000L)
        return 20;
    }

    /*
    * async style fuctions
    * 이 방법은 권장하지 않음, xxxAsync 함수는 suspend 함수가 아님, 어디서든 사용할 수 있음
    * 외부에서 Exception이 발생해도 감지 못함
    */
    fun test3() {
        val time = measureTimeMillis {
            val one = SomethingUseFulOneAsync()
            val two = SomethingUseFulTwoAsync()

            runBlocking {
                println("블라 브랄 ")
            }
        }
    }

    fun SomethingUseFulOneAsync() = GlobalScope.async { doSomethingUseFulOne() }
    fun SomethingUseFulTwoAsync() = GlobalScope.async { doSomethingUseFulTwo() }


    /*
    * Structured concurrency with async
    * This way, if throws an exception, all the coroutines will be cacelled.
    */
    fun test4() {
        runBlocking {
            val time = measureTimeMillis {
                println("The answer is ${concurrentSum()}")
            }
            println("Completed in $time ms")
        }
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUseFulOne() }
        val two = async { doSomethingUseFulTwo() }
        one.await() + two.await()
    }

    /*
    * Cancellation propagated coroutines hierarchy
    * Exception 발생 시 부모까지 전파됨
    * */
    fun test5() {
        runBlocking {
            try {
                failedConcurrentSum()
            } catch (e: ArithmeticException) {
                println("Computation failed with ArithmeticException")
            }
        }
    }

    suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async<Int> {
            try {
                delay(Long.MAX_VALUE)
                42
            } finally {
                println("First child was cancelled")
            }
        }
        val two = async<Int> {
            println("Second child throws in exception")
            throw ArithmeticException()
        }
        one.await() + two.await()
    }
}
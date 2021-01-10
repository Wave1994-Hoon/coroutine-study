package study.coroutine

import kotlinx.coroutines.*

/*
* Summary
* - Job
* cancel()
*
* - Cancellation is cooperative
* way 1: to periodically invoke a suspending
* way 2: explicitly check the cancellation status(isActive)
*
* - Timeout
* withTImeout
* withTImeoutOrNull
*/
class cancellationAndTimeOuts {

    fun test() {
        runBlocking {
            val job = launch {
                repeat(10000) { i ->
                    println("Job: I'm sleeping $i ...")
                    delay(500L)
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            /* Job that can ve used to cancel the running coroutine */
            job.cancel()
            job.join()
            println("main: Now I can quit.")
        }
    }

    fun test2() {
        /*
        * suspend 함수가 없기 때문에 cancel이 되지 않는다.
        * cancel을 하게 되면 suspend funciton에서 Exception을 발생시킴
        */
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        // delay(1L) or yield()
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting !")
            job.cancelAndJoin()
        }
    }

    fun test3() {
        /*
        * isActive를 사용해서 cancel 가눙 , Exception은 던지지 않는다.
        * isActive: 코투틴이 종료되었는지 체크, 확장 프로퍼티
        */
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting !")
            job.cancelAndJoin()
        }
    }

    /* runBocking 내부에서 time out을 직접 걸 수 있다.*/
    fun test4() {
        runBlocking {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        }
    }
    /* Exception 발생하면 Null 리턴 */
    fun test5() {
        runBlocking {
            withTimeoutOrNull(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        }
    }
}
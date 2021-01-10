package study.coroutine

import kotlinx.coroutines.*

class ContextAndDispatchers {
    /*
    * Dispatchers and threads
    * The coroutine context includes a coroutine dispatcher
    * determines what thread for its execution
    * All coroutine builders an optional CoroutineContext parameter
    * that can be used to explicitly specify the dispatcher
    */
    fun test() {
        runBlocking {
            /* 자신을 호출했던 코루틴 스코프에서 context를 상속 받음 -> runBlocking이랑 같음 */
            launch {
                println("main runBlocking")
            }
            launch(Dispatchers.Unconfined) {
                println("Unconfined")
            }
            launch(Dispatchers.Default) {
                println("Default")
            }
            /* 코루틴을 만들때마다 Thread를 만든다 -> 비용 큼, close 하기 위해서 use 사용  */
            newSingleThreadContext("MyOwnThread").use {
                launch(it) {
                    println("newSingleThreadContext")
                }
            }
        }
    }

    /* Coroutine Scope */
    private val mainScope = CoroutineScope(Dispatchers.Default)

    fun test2() {
        mainScope.cancel()
    }
}
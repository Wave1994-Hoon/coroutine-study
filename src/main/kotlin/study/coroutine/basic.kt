package study.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class basic {
    fun test() {
        /*
        * GlobalScope: 프로그램이 끝날때까지 계속 실행
        * launch: 코투틴 빌더, 빌더 내에서 로직 실행
        */
        GlobalScope.launch {
            delay(1000L)
            println("HI")
        }

        println("Hi HI")
        Thread.sleep(2000L)

        /* GlobalScope.lanuch {}는 => thread {} 로 대체 가능  */
        thread {
            Thread.sleep(2000L)
            println("Hi")
        }

        println("HI HI")
        Thread.sleep(2000L)

        /*
        * Thread Blocking 기능을 가지고 있음.
        * RunBlocking이 끝나기 전까지 다음 코드로 넘어가지 않음
        */
        runBlocking {
            delay(1000L)
        }

        /* 관용적인 스타일 */
        runBlocking {
            GlobalScope.launch {
                delay(1000L)
                println("HI")
            }
            println("HI HI HI")
            delay(2000L)
        }

        /* without delay */
        val job = GlobalScope.launch {
            delay(2000L)
            println("Hi")
        }

        println("Hello")
        /* job이 끝날때까지 기다리다가 실행 */
        job.join();

        /*
        * struct concurrency
        * join을 사용하지 않고도 같은 효과를 볼 수 있다.
        * Top 레벨 코루틴을 만들지 않고 chilD 코투틴을 가지고 있는 코루틴을 만들면 종료될 때까지 기다려즘
        */
        runBlocking {
            this.launch {
                delay(1000L)
                println("World!")
            }
            this.launch {
                delay(1000L)
                println("World")
            }
        }

        println("Hello~~")

        /* suspend function example*/
        runBlocking {
            this.launch {
                myWorld()
            }
        }
    }
    suspend fun myWorld() {
        delay(1000L)
        println("Hello")
    }

}
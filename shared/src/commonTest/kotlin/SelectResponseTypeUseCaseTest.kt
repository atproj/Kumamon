import com.example.kumamon.SelectResponseTypeUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectResponseTypeUseCaseTest {

    private lateinit var selectResponseTypeUseCase: SelectResponseTypeUseCase

    @BeforeTest
    fun start() {
        selectResponseTypeUseCase = SelectResponseTypeUseCase()
    }

    @Test
    fun `a message including an image request should return an image response type`() {
        val responseType = selectResponseTypeUseCase.invoke("Show me a picture of Kumamoto")
        assertEquals(responseType, SelectResponseTypeUseCase.Response.IMAGE)
    }

    @Test
    fun `a normal message should return a text response type`() {
        val responseType = selectResponseTypeUseCase.invoke("what is your favorite sport?")
        assertEquals(responseType, SelectResponseTypeUseCase.Response.TEXT)
    }
}
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

    @Test
    fun `a message prefixed with a translation prompt should return a translation response type`() {
        val responseType = selectResponseTypeUseCase.invoke("Translate in japanese May I request a beer?")
        assertEquals(responseType, SelectResponseTypeUseCase.Response.TRANSLATION)
    }
}
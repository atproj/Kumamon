package com.example.kumamon.android

import com.example.kumamon.SelectResponseTypeUseCase
import com.example.kumamon.data.ImageResponse
import com.example.kumamon.data.LangMod
import com.example.kumamon.model.Chat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private val useCase = mockk<SelectResponseTypeUseCase>(relaxed=true)
    private val model = mockk<LangMod>(relaxed = true)
    private lateinit var vm: MainViewModel

    @Before
    fun setup() {
        val testDispatcher = StandardTestDispatcher()
        vm = MainViewModel(useCase, model, testDispatcher)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `asking a regular question results in a text reply`() = runTest {
        val outgoingMsg = "What is your favorite sport?"
        every { useCase.invoke(outgoingMsg) } returns SelectResponseTypeUseCase.Response.TEXT
        val textReply = "Basketball"
        coEvery { model.converse(outgoingMsg) } returns textReply

        vm.onSubmit(outgoingMsg)
        advanceUntilIdle()

        verify { useCase.invoke(outgoingMsg) }
        coVerify { model.converse(outgoingMsg) }
        val expectedReply = Chat(message = textReply, fromUser = false)
        val actualState = vm.conversation.value as ResultState.Success.NonEmpty
        val actualReply = actualState.value.last()
        assertEquals(expectedReply.message, actualReply.message)
    }

    @Test
    fun `requesting an image results in an image reply`() = runTest {
        every { useCase.invoke(any()) } returns SelectResponseTypeUseCase.Response.IMAGE
        val imageResponse = mockk<ImageResponse>()
        val imageUrl = "images.com/xyz"
        every { imageResponse.imageUrl } returns imageUrl
        coEvery { model.replyImage(any()) } returns imageResponse

        vm.onSubmit("Show me what kumamon steamed buns look like")
        advanceUntilIdle()

        verify { useCase.invoke(any()) }
        coVerify { model.replyImage(any()) }
        val expectedReply = Chat(message = "", imageUrl = imageUrl, fromUser = false)
        val actualState = vm.conversation.value as ResultState.Success.NonEmpty
        val actualReply = actualState.value.last()
        assertEquals(expectedReply.imageUrl, actualReply.imageUrl)
    }

    @Test
    fun `requesting a translation results in a translation reply`() = runTest {
        every { useCase.invoke(any()) } returns SelectResponseTypeUseCase.Response.TRANSLATION
        val translationResponse = "japanese response"
        coEvery { model.converse(any()) } returns translationResponse

        vm.onSubmit("Translate in japanese 'I want ikinari dango please'")
        advanceUntilIdle()

        verify { useCase.invoke(any()) }
        coVerify { model.converse(any()) }
        val expectedReply = Chat(message = translationResponse, fromUser = false,
            enableDictation = true)
        val actualState = vm.conversation.value as ResultState.Success.NonEmpty
        val actualReply = actualState.value.last()
        assertEquals(expectedReply.message, actualReply.message)
    }

    @Test
    fun `a failure in querying the model results in an error state`() = runTest {
        every { useCase.invoke(any()) } returns SelectResponseTypeUseCase.Response.TEXT
        val exception = IllegalStateException("")
        coEvery { model.converse(any()) } throws exception

        vm.onSubmit("Are you a fan of the volters?")
        advanceUntilIdle()

        verify { useCase.invoke(any()) }
        coVerify { model.converse(any()) }
        assertEquals(vm.conversation.value, ResultState.Failure(exception))
    }
}
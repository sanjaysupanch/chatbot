package com.example.chatbot.di

import com.example.chatbot.data.network.NetworkMonitor
import com.example.chatbot.data.queue.InMemoryMessageQueue
import com.example.chatbot.data.repository.ChatRepositoryImpl
import com.example.chatbot.data.socket.PieSocketClient
import com.example.chatbot.data.socket.RealTimeClient
import com.example.chatbot.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRealTimeClient(): RealTimeClient {
        return PieSocketClient()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        client: RealTimeClient,
        queue: InMemoryMessageQueue,
        monitor: NetworkMonitor
    ): ChatRepository {
        return ChatRepositoryImpl(client, queue, monitor)
    }
}
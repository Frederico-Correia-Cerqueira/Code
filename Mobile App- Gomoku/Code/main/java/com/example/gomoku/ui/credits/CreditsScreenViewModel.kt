package com.example.gomoku.ui.credits

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gomoku.InformationService
import com.example.gomoku.TAG
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Idle
import com.example.gomoku.domain.Information
import com.example.gomoku.domain.idle
import com.example.gomoku.domain.loaded
import com.example.gomoku.domain.loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreditsScreenViewModel(private val service: InformationService) : ViewModel() {

    companion object {
        fun factory(service: InformationService) = viewModelFactory {
            initializer { CreditsScreenViewModel(service) }
        }
    }

    /**
     * Represents the information to be displayed, fetched via the provided service.
     * Its state is managed by a [IOState].
     */
    var info by mutableStateOf<IOState<Information>>(idle())
        private set

    private val _errorFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * The flow of states the view model traverses.
     */
    val error: Flow<Boolean>
        get() = _errorFlow.asStateFlow()


    fun fetchAuthors() {
        if (info !is Idle) {
            throw IllegalStateException("The view model is not in the idle state.")
        }
        info = loading()
        viewModelScope.launch {
            val result = runCatching { service.fetchCredits() }
            if (result.isSuccess) {
                info = loaded(result)
                //Log.v(TAG, "fetching Authors")
            } else {
                _errorFlow.value = true
                Log.e(TAG, "Error fetching player stats")
            }

        }
    }
}
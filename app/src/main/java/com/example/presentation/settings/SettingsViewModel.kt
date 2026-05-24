package com.example.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _aiEnhancementEnabled = MutableStateFlow(false)
    val aiEnhancementEnabled: StateFlow<Boolean> = _aiEnhancementEnabled

    private val _hardwareAccelerationEnabled = MutableStateFlow(true)
    val hardwareAccelerationEnabled: StateFlow<Boolean> = _hardwareAccelerationEnabled

    fun toggleAiEnhancement(enabled: Boolean) {
        _aiEnhancementEnabled.value = enabled
    }

    fun toggleHardwareAcceleration(enabled: Boolean) {
        _hardwareAccelerationEnabled.value = enabled
    }
}

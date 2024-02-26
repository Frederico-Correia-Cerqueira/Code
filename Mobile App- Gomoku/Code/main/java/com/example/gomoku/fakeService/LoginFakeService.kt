package com.example.gomoku.fakeService

import com.example.gomoku.LoginService
import com.example.gomoku.domain.LoginDto
import com.example.gomoku.domain.LogoutDto

class LoginFakeService: LoginService {
    override suspend fun fetchLogin(name: String, password: String) = LoginDto("123-123-123-123", 1)
    override suspend fun fetchLogout(token: String) = LogoutDto(true)
}
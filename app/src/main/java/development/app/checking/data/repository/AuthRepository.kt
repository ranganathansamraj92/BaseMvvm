package development.app.checking.data.repository

import development.app.checking.data.request.LoginRequest
import development.app.checking.data.source.remote.APIResponse
import development.app.checking.data.source.remote.AuthApiCallInterface
import development.app.checking.viewmodel.LoginViewModel

class AuthRepository(private val apiService: AuthApiCallInterface): BaseRepository() {


    suspend fun login(loginRequest: LoginRequest): APIResponse? {
        return safeApiCall(call = { apiService.loginAsync(loginRequest).await() })
    }


}
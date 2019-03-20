package development.app.checking.viewmodel.BaseViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import development.app.checking.data.source.remote.*
import development.app.checking.viewmodel.DetailViewModel
import development.app.checking.viewmodel.LoginViewModel
import development.app.checking.viewmodel.SplashViewModel
import development.app.checking.viewmodel.VersionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel() {

    private val injector: ViewModelInjector = DaggerViewModelInjector.builder()
        .networkModule(networkModule = NetworkModule)
        .localNetworkModule(localNetworkModule = LocalNetworkModule)
        .build()

    init {
        inject()
    }


    fun inject() {
        when (this) {
            is SplashViewModel -> injector.inject(this)

            is VersionViewModel -> injector.inject(this)

            is DetailViewModel -> injector.inject(this)

            is LoginViewModel -> injector.inject(this)
        }

    }

    open var baseApiResponse: MutableLiveData<APIResponse> = MutableLiveData()

    open var loadingStatus: MutableLiveData<Boolean> = MutableLiveData()
    open var errorStatus: MutableLiveData<String> = MutableLiveData()

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    val scope = CoroutineScope(coroutineContext)


    open fun cancelAllRequests() = coroutineContext.cancel()

    override fun onCleared() {
        super.onCleared()
        Log.w("TAG", "OnCleared")
        cancelAllRequests()

    }

    open fun handleResponses(apiResponse: APIResponse): APIResponse {

        when (apiResponse) {

            is APIResponse.Success -> {
                loadingStatus.postValue(false)
                apiResponse.successResult as APIResponse?
                return if (apiResponse.successResult.meta.status) {
                    apiResponse.successResult
                } else {
                    errorStatus.postValue(apiResponse.successResult.meta.message)
                    apiResponse.successResult
                }
            }

            is APIResponse.Error -> {
                loadingStatus.postValue(false)
                errorStatus.postValue(apiResponse.errorMessage as String)
            }

            is APIResponse.Exception -> {
                loadingStatus.postValue(false)
                errorStatus.postValue(apiResponse.exception as String)
            }


        }
        return apiResponse
    }
}
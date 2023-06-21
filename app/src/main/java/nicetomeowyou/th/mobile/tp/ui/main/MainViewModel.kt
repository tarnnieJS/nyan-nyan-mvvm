import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nicetomeowyou.th.mobile.tp.services.api.ServiceClient
import nicetomeowyou.th.mobile.tp.services.models.Product
import retrofit2.HttpException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val productData = MutableLiveData<Product>()
    val error = MutableLiveData<String>()

    fun fetchProductData() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ServiceClient.create(getApplication<Application>().baseContext).getProduct()
                }
                productData.value = response
            } catch (ex: Exception) {
                val errorMessage = if (ex is HttpException) {
                    "(${ex.code()}): $ex"
                } else {
                    ex.toString()
                }
                error.value = errorMessage
            }
        }
    }

    fun signOut() {
        val googleSignInClient = GoogleSignIn.getClient(
            getApplication(),
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
        viewModelScope.launch {
            try {
                googleSignInClient.signOut()

                // Perform any other necessary actions after sign-out
            } catch (ex: Exception) {
                // Handle sign-out failure
            }
        }
    }
}

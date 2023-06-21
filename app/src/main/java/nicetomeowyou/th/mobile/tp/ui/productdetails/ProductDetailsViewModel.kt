package nicetomeowyou.th.mobile.tp.ui.productdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nicetomeowyou.th.mobile.tp.services.api.ServiceClient
import nicetomeowyou.th.mobile.tp.services.models.ProductDetails
import nicetomeowyou.th.mobile.tp.services.models.Request
import retrofit2.HttpException

class ProductDetailsViewModel : ViewModel() {
    val productDetails = MutableLiveData<ProductDetails>()
    val error = MutableLiveData<String>()

    fun fetchProductDetails(type: Int?) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    when (type) {
                        1 -> ServiceClient.create(ProductDetailsActivity().applicationContext).getIphoneDetails(Request(""))
                        2 -> ServiceClient.create(ProductDetailsActivity().applicationContext).getIpadDetails(Request(""))
                        else -> ServiceClient.create(ProductDetailsActivity().applicationContext).getAppleWatchDetails(Request(""))
                    }
                }
                productDetails.value = response
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
}

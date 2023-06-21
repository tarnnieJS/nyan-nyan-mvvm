package nicetomeowyou.th.mobile.tp.ui.productdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import nicetomeowyou.th.mobile.tp.databinding.ActivityProductDetailsBinding
import nicetomeowyou.th.mobile.tp.services.models.ProductDetails

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var viewModel: ProductDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra("productName")
        val productType = intent.getIntExtra("productType", 0)

        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]
        viewModel.productDetails.observe(this, Observer { productDetails ->
            displayProductDetails(productDetails)
        })
        viewModel.error.observe(this, Observer { errorMessage ->
            showError(errorMessage)
        })

        binding.ProductNameTitle.text = productName
        binding.buttonBack.setOnClickListener {
            finish()
        }

        viewModel.fetchProductDetails(productType)
    }

    private fun displayProductDetails(productDetails: ProductDetails) {
        binding.textViewProductDescription.text = productDetails.productDetail
        Picasso.get().load(productDetails.productImage).into(binding.imageViewProductDetail)
    }

    private fun showError(errorMessage: String) {
        Log.e("HttpException", errorMessage)
    }
}

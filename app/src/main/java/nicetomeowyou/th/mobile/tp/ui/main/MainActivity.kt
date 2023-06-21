package nicetomeowyou.th.mobile.tp.ui.main

import MainViewModel
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import nicetomeowyou.th.mobile.tp.ui.auth.AuthActivity
import nicetomeowyou.th.mobile.tp.R
import nicetomeowyou.th.mobile.tp.adapter.BannerAdapter
import nicetomeowyou.th.mobile.tp.adapter.ProductAdapter
import nicetomeowyou.th.mobile.tp.databinding.ActivityMainBinding
import nicetomeowyou.th.mobile.tp.services.models.ProductX
import nicetomeowyou.th.mobile.tp.ui.productdetails.ProductDetailsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var productAdapter: ProductAdapter
    private var listProduct = listOf<ProductX>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayName = intent.getStringExtra("displayName")

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.productData.observe(this, Observer { productResponse ->
            productResponse?.let {
                displayProductData(it.products, it.banners)
            }
        })
        viewModel.error.observe(this, Observer { errorMessage ->
            showError(errorMessage)
        })

        binding.textViewUserName.text = "สวัสดีคุณ $displayName"
        binding.buttonLogOut.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(this@MainActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchProductData()
    }

    private fun displayProductData(products: List<ProductX>, banners: List<String>) {
        listProduct = products
        setUpProductList(listProduct)
        initialBanner(banners.reversed())
        setupIndicators(banners.size)
        setCurrentIndicator(0)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        // Handle error display or any other necessary action
    }

    private fun setUpProductList(listProduct: List<ProductX>) {
        binding.recycleViewProduct.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(listProduct, object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, model: ProductX) {
                intentActivity(model.productName)
            }
        })
        binding.recycleViewProduct.adapter = productAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchProductData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun intentActivity(productName: String) {
        val intent = Intent(this@MainActivity, ProductDetailsActivity::class.java)
        val productType = when (productName) {
            "iPhone 11 Pro" -> 1
            "iPad Pro" -> 2
            else -> 3
        }
        intent.putExtra("productName", productName)
        intent.putExtra("productType", productType)
        startActivity(intent)
    }

    private fun initialBanner(listBannerImage: List<String>) {
        val banner = binding.viewPagerBanner
        banner.isUserInputEnabled = true
        val banners: List<String> = listBannerImage
        val adapter = BannerAdapter(banners)
        banner.adapter = adapter

        banner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(size: Int) {
        binding.indicatorContainer.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(size)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(2, 8, 2, 8)
        for (x in indicators.indices) {
            indicators[x] = ImageView(applicationContext)
            indicators[x].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.permission_indictor_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicatorContainer.addView(indicators[x])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.permission_indictor_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.permission_indictor_inactive
                    )
                )
            }
        }
    }
}

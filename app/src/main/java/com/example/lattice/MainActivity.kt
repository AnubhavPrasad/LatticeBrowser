package com.example.lattice

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.lattice.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.progressBar.max = 100
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.setSupportMultipleWindows(true)
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.setBackgroundColor(Color.WHITE)
        if (savedInstanceState != null)
            binding.webView.restoreState(savedInstanceState)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressBar.progress = newProgress
                }
                if (newProgress == 100) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                CookieManager.getInstance().acceptCookie()
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.stop.visibility = View.VISIBLE
                binding.refresh.visibility = View.GONE
                binding.webAddressEditText.setText(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.webAddressEditText.setText(url)
                binding.stop.visibility = View.GONE
                binding.refresh.visibility = View.VISIBLE
            }

        }
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if(scrollY>oldScrollY){
                binding.upper.visibility=View.GONE
            }
            if(scrollY==0){
                binding.upper.visibility=View.VISIBLE
            }
        }
        binding.goButton.setOnClickListener {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                binding.webAddressEditText.windowToken,
                0
            )
            if (binding.webAddressEditText.text.toString().toLowerCase().contains("https")) {
                binding.webView.loadUrl(binding.webAddressEditText.text.toString())
            } else {
                binding.webView.loadUrl("https://" + binding.webAddressEditText.text.toString())
            }
        }
        binding.webView.loadUrl("https://google.com")
        binding.backArrow.setOnClickListener {
            binding.webView.goBack()
        }

        binding.forwardArrow.setOnClickListener {
            binding.webView.goForward()
        }
        binding.home.setOnClickListener {
            binding.webView.loadUrl("https://google.com")
        }
        binding.refresh.setOnClickListener {
            binding.webView.reload()
        }
        binding.stop.setOnClickListener {
            binding.webView.stopLoading()
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }

    }
}


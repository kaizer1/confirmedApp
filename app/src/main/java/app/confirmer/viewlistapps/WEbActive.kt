package app.confirmer.viewlistapps

import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import app.confirmer.R

class WEbActive : AppCompatActivity() {


    lateinit var myWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.web_a)

        myWebView = findViewById(R.id.weba)


        val extras = intent.extras


        val jejHFBE = CookieManager.getInstance()
        jejHFBE.setAcceptCookie(true)

        myWebView?.settings?.javaScriptEnabled = true
        myWebView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        myWebView?.settings?.domStorageEnabled = true
        myWebView?.settings?.allowFileAccess = true

        myWebView?.settings?.useWideViewPort = true
        myWebView?.settings?.loadWithOverviewMode = true
        myWebView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        myWebView?.settings?.builtInZoomControls = true


        myWebView.webChromeClient = object : WebChromeClient(){



        }



        myWebView.webViewClient = object : WebViewClient() {

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)

            }


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                println(" my request == ${request!!.url}")

            return false


            }



        } // end requesr







        if (extras != null) {

            println(" my link == ${extras.getString("link_al")}")
               myWebView.loadUrl(extras.getString("link_al").toString())
         // value = extras.getString("key");
        }

    }


}
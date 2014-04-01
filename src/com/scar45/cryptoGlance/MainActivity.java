package com.scar45.cryptoGlance;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.BaseMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    protected FrameLayout webViewPlaceholder;
	protected WebView webView;
	protected View cryptoGlanceWebview;
	protected ViewGroup parentViewGroup;

    ProgressBar loadingProgressBar,loadingTitle;

    // TODO: Send login credentials, but MUST be secured first!
    // String urlUsercryptoGlance = "";

    String urlcryptoGlance = "http://cryptoglance.info";
    String urlSubreddit = "http://reddit.com/r/cryptoglance";
    String urlGooglePlus = "https://plus.google.com/u/0/b/110896112995796953409/110896112995796953409/posts";
    String urlGooglePlusCommunity = "https://plus.google.com/u/0/b/110896112995796953409/communities/111042089628113521779";
    String urlTwitter = "http://twitter.com/cryptoGlance";
    String urlGithub = "https://github.com/cryptoGlance";

    protected void checkPrefs() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String urlUsercryptoGlance = sharedPrefs.getString("cg_url", "");
        boolean urlUseHTTPS = sharedPrefs.getBoolean("use_https", false);
		
        // Check the status of preferences, and either load App Settings if empty, or load the user-defined URL of cryptoGlance
        
        if (urlUsercryptoGlance != null && !urlUsercryptoGlance.isEmpty()) {
            if (urlUseHTTPS == true) {
                Toast.makeText(getApplicationContext(),
                "Loading '" + urlUsercryptoGlance + "' using secure HTTPS",
                Toast.LENGTH_LONG).show();
                webView.loadUrl("https://" + urlUsercryptoGlance);
            } else {
                Toast.makeText(getApplicationContext(),
                "Loading '" + urlUsercryptoGlance + "' using normal HTTP",
                Toast.LENGTH_LONG).show();
                webView.loadUrl("http://" + urlUsercryptoGlance);                
            }
        } else {
            Toast.makeText(getApplicationContext(),
            "cryptoGlance URL is not defined! Please check the App Settings.",
            Toast.LENGTH_LONG).show();            
        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_preserving_impl);

		// Initialize the UI
		initUI();
	}

    protected void initUI()
	{
		// Retrieve UI elements
		webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));
        
        // Initialize the WebView if necessary
		if (cryptoGlanceWebview == null)
		{
			// Create the webview
            setContentView(R.layout.activity_main);
        	cryptoGlanceWebview = (View) findViewById(R.id.cryptoGlanceWebview);
            parentViewGroup = (ViewGroup)cryptoGlanceWebview.getParent();
			webView = (WebView) findViewById(R.id.webview);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings().setBuiltInZoomControls(false);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setPluginsEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            
            // Enable downloads of files within webView
            webView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                        String contentDisposition, String mimetype,
                        long contentLength) {
                  Intent i = new Intent(Intent.ACTION_VIEW);
                  i.setData(Uri.parse(url));
                  startActivity(i);
                }
            });


			// Attach the ProgressBar layout
            loadingProgressBar=(ProgressBar)findViewById(R.id.progressbar_Horizontal);

            webView.setWebChromeClient(new WebChromeClient() {

                // this will be called on page loading progress
                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                    super.onProgressChanged(view, newProgress);

                    loadingProgressBar.setProgress(newProgress);

                    // hide the progress bar if the loading is complete
                    if (newProgress == 100) {
                    loadingProgressBar.setVisibility(View.GONE);
                    } else{
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                
            });   

            webView.setWebViewClient(new WebViewClient() {
            
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                view.loadUrl(url);
                    return true;
		        }

                @Override
                public void onPageFinished(WebView view, String url) 
                {
                    String command = "javascript:fixApp();";
                    view.loadUrl(command);       
                }
    
    	        });
	        
            onResume();
            
		}
		parentViewGroup.removeView(cryptoGlanceWebview);
		// Attach the WebView to its placeholder
		parentViewGroup.addView(cryptoGlanceWebview);
	}
        
    @Override
    protected void onResume() {
        super.onResume();
        
        final String PREFS_NAME = "FirstRunCheck";
        SharedPreferences firstrun = getSharedPreferences(PREFS_NAME, 0);
        if (firstrun.getBoolean("first_time_run", true)) {
            Toast.makeText(getApplicationContext(),
            "This is your first time running cryptoGlance. Please set the location of your cryptoGlance instance.",
            Toast.LENGTH_LONG).show();
            
            startActivity(new Intent(this, ShowSettingsActivity.class));

            firstrun.edit().putBoolean("first_time_run", false).commit(); 
        } else {
            checkPrefs();
        }
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		if (webView != null)
		{
			// Remove the WebView from the old placeholder
			parentViewGroup.removeView(cryptoGlanceWebview);
		}

		// Load the layout resource for the new configuration
        setContentView(R.layout.state_preserving_impl);

		// Reinitialize the UI
		initUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		webView.restoreState(savedInstanceState);
        
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // Menu Selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about:
                showDialog(11);
                break;

            case R.id.action_appsettings:
    			startActivity(new Intent(this, ShowSettingsActivity.class));
    			return true;

            case R.id.action_reload:
                checkPrefs();          

                //webView.reload();
                break;

            case R.id.action_exit:
                Toast.makeText(getApplicationContext(),
                "Thanks for using cryptoGlance!",
                Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
                break;

            case R.id.action_sitemenu:
                webView.loadUrl("javascript:toggleMobileNavbar();");
                break;
                                
            default:
                break;

        }
        return true;
    }

    public void onBackPressed (){
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();       
        } else {
            MainActivity.this.finish();
        }
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {    
            case 11:
            // Create our About Dialog
            TextView aboutMsg  = new TextView(this);
            aboutMsg.setMovementMethod(LinkMovementMethod.getInstance());
            aboutMsg.setPadding(30, 30, 30, 30);
            aboutMsg.setText(Html.fromHtml("<big>A simple app which gives you quick access to your cryptoGlance installation.<br><br><font color='white'>Find us on</font> <a href=\""+urlTwitter+"\">Twitter</a><font color='white'>, <font color='white'>Add us to your circles on</font> <a href=\""+urlGooglePlus+"\">Google+</a> / <a href=\""+urlGooglePlusCommunity+"\">(G+ Community)</a><font color='white'>, <font color='white'>Subscribe to us on</font> <a href=\""+urlSubreddit+"\">Reddit</a><font color='white'>, and find our source on</font> <a href=\""+urlGithub+"\">Github</a><font color='white'>.</font><br><br><font color='white'>Please consider</font> <b>donating</b> <font color='white'>, as your contribution would surely help <em>a lot!</em></font></big>"));

            Builder builder = new AlertDialog.Builder(this);
                builder.setView(aboutMsg)
                .setTitle(Html.fromHtml("About <b><font color='" + getResources().getColor(R.color.cryptoGlance_lightgrey) + "'>cryptoGlance</font></b>"))
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("cryptoGlance Homepage",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                int which) {
		                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlcryptoGlance));
		                            startActivity(intent);
                            }
                        })
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                    Toast.makeText(getApplicationContext(),
                                    "Thanks for using cryptoGlance!",
                                    Toast.LENGTH_LONG).show();
                            }
                        });

            return builder.create();
        }

        return super.onCreateDialog(id);
    }

}

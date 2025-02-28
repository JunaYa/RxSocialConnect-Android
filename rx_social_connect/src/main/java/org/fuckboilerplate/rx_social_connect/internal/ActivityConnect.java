/*
 * Copyright 2016 FuckBoilerplate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fuckboilerplate.rx_social_connect.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;

import org.fuckboilerplate.rx_social_connect.R;
import org.fuckboilerplate.rx_social_connect.internal.services.Service;

import rx.Subscriber;

public class ActivityConnect extends Activity {
    public static final String KEY_RESULT = "key_result";
    public static Service<? extends Token, ? extends OAuthService> service;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_activity);
        initWebView();
    }

    protected void initWebView() {
        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith(service.callbackUrl())) return super.shouldOverrideUrlLoading(view, url);

                webView.setVisibility(View.GONE);

                service.oResponse(url).subscribe(new Subscriber<Token>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable error) {
                        finishWithError(error);
                    }

                    @Override public void onNext(Token token) {
                        finishWithToken(token);
                    }
                });

                return true;
            }

            @Override public void onPageFinished(WebView view, String url) {}
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        service.oAuthUrl().subscribe(new Subscriber<String>() {
            @Override public void onCompleted() {}

            @Override public void onError(Throwable error) {
                finishWithError(error);
            }

            @Override public void onNext(String url) {
                webView.loadUrl(url);
            }
        });
    }

    private void finishWithError(Throwable error) {
        Intent intent = new Intent();

        String message = error.getMessage();
        if (message == null && error.getCause() != null) message = error.getCause().getMessage();

        intent.putExtra(KEY_RESULT, message);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void finishWithToken(Token token) {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT, token);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
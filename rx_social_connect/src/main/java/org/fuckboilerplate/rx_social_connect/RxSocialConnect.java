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

package org.fuckboilerplate.rx_social_connect;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;

import org.fuckboilerplate.rx_social_connect.internal.ActivityConnect;
import org.fuckboilerplate.rx_social_connect.internal.persistence.OAuth2AccessToken;
import org.fuckboilerplate.rx_social_connect.internal.persistence.TokenCache;
import org.fuckboilerplate.rx_social_connect.internal.services.OAuth1Service;
import org.fuckboilerplate.rx_social_connect.internal.services.OAuth2Service;
import org.fuckboilerplate.rx_social_connect.internal.services.Service;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx_activity_result.Result;
import rx_activity_result.RxActivityResult;

public final class RxSocialConnect {
    /**
     * Register RxSocialConnect calling this method on onCreate android application method.
     */
    public static void register(Application application) {
        RxActivityResult.register(application);
        TokenCache.INSTANCE.init(application);
    }

    /**
     * Performs an oauth http call using oauth1 protocol and retrieve the associated token calling it from an activity.
     * @param activity the activity calling
     * @param oAuth10aService the oauth1 service containing the provider as long as the provider credentials.
     * @param <A> the activity calling
     * @return a response instance holding the OAuth1AccessToken and the current valid activity instance.
     * @see OAuth1AccessToken
     */
    public static <A extends Activity> Observable<Response<A, OAuth1AccessToken>> with(A activity, OAuth10aService oAuth10aService) {
        return startActivity(activity, new OAuth1Service(oAuth10aService),
                oAuth10aService.getApi().getClass().getSimpleName(), OAuth1AccessToken.class)
                .map(new Func1<Response<Object, OAuth1AccessToken>, Response<A, OAuth1AccessToken>>() {
                    @Override public Response<A, OAuth1AccessToken> call(Response<Object, OAuth1AccessToken> response) {
                        return new Response(response.targetUI(), response.token());
                    }
                });
    }

    /**
     * Performs an oauth http call using oauth2 protocol and retrieve the associated token calling it from an activity.
     * @param activity the activity calling
     * @param oAuth20Service the oauth2 service containing the provider as long as the provider credentials.
     * @param <A> the activity calling
     * @return a response instance holding the OAuth1AccessToken and the current valid activity instance.
     * @see com.github.scribejava.core.model.OAuth2AccessToken
     */
    public static <A extends Activity> Observable<Response<A, com.github.scribejava.core.model.OAuth2AccessToken>> with(A activity, OAuth20Service oAuth20Service) {
        return startActivity(activity, new OAuth2Service(oAuth20Service),
                oAuth20Service.getApi().getClass().getSimpleName(), OAuth2AccessToken.class)
                .map(new Func1<Response<Object, OAuth2AccessToken>, Response<A, com.github.scribejava.core.model.OAuth2AccessToken>>() {
                    @Override public Response<A, com.github.scribejava.core.model.OAuth2AccessToken> call(Response<Object, OAuth2AccessToken> response) {
                        return new Response(response.targetUI(), response.token());
                    }
                });
    }

    /**
     * Performs an oauth http call using oauth1 protocol and retrieve the associated token calling it from a fragment.
     * @param fragment the fragment calling
     * @param oAuth10aService the oauth1 service containing the provider as long as the provider credentials.
     * @param <F> the fragment calling
     * @return a response instance holding the OAuth1AccessToken and the current valid activity fragment.
     * @see OAuth1AccessToken
     */
    public static <F extends Fragment> Observable<Response<F, OAuth1AccessToken>> with(F fragment, OAuth10aService oAuth10aService) {
        return startActivity(fragment, new OAuth1Service(oAuth10aService),
                oAuth10aService.getApi().getClass().getSimpleName(), OAuth1AccessToken.class)
                .map(new Func1<Response<Object, OAuth1AccessToken>, Response<F, OAuth1AccessToken>>() {
                    @Override public Response<F, OAuth1AccessToken> call(Response<Object, OAuth1AccessToken> response) {
                        return new Response(response.targetUI(), response.token());
                    }
                });
    }

    /**
     * Performs an oauth http call using oauth2 protocol and retrieve the associated token calling it from a fragment.
     * @param fragment the fragment calling
     * @param oAuth20Service the oauth2 service containing the provider as long as the provider credentials.
     * @param <F> the fragment calling
     * @return a response instance holding the OAuth2AccessToken and the current valid activity fragment.
     * @see com.github.scribejava.core.model.OAuth2AccessToken
     */
    public static <F extends Fragment> Observable<Response<F, com.github.scribejava.core.model.OAuth2AccessToken>> with(F fragment, OAuth20Service oAuth20Service) {
        return startActivity(fragment, new OAuth2Service(oAuth20Service),
                oAuth20Service.getApi().getClass().getSimpleName(), OAuth2AccessToken.class)
                .map(new Func1<Response<Object, OAuth2AccessToken>, Response<F, com.github.scribejava.core.model.OAuth2AccessToken>>() {
                    @Override public Response<F, com.github.scribejava.core.model.OAuth2AccessToken> call(Response<Object, OAuth2AccessToken> response) {
                        return new Response(response.targetUI(), response.token());
                    }
                });
    }

    /**
     * Remove an stored token from a previous oauth authentication cached on disk.
     * @see BaseApi
     */
    public static Observable<Void> closeConnection(final Class<? extends BaseApi> classApi) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override public Observable<Void> call() {
                String keyToken = classApi.getSimpleName();
                TokenCache.INSTANCE.evict(keyToken);
                return Observable.just(null);
            }
        });
    }

    /**
     * Remove all stored tokens from previous oauth authentications cached on disk.
     */
    public static Observable<Void> closeConnections() {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override public Observable<Void> call() {
                TokenCache.INSTANCE.evictAll();
                return Observable.just(null);
            }
        });
    }

    /**
     * Retrieve the token stored resulting from previous Oauth1 authentication.
     * @param classApi a class provider which extends from DefaultApi10a. The same one used to build the OAuthService.
     * @return observable containing an OAuth1AccessToken or if not token cached observable which throws NotTokenFoundException
     */
    public static Observable<OAuth1AccessToken> getTokenOAuth1(final Class<? extends DefaultApi10a> classApi) {
        return Observable.defer(new Func0<Observable<OAuth1AccessToken>>() {
            @Override public Observable<OAuth1AccessToken> call() {
                String keyToken = classApi.getSimpleName();

                Observable<OAuth1AccessToken> token = (Observable<OAuth1AccessToken>) TokenCache.INSTANCE.get(keyToken, OAuth1AccessToken.class);
                if (token != null) return token;

                return Observable.error(new NotActiveTokenFoundException());
            }
        });
    }

    /**
     * Retrieve the token stored resulting from previous Oauth2 authentication.
     * @param classApi a class provider which extends from DefaultApi20. The same one used to build the OAuthService.
     * @return observable containing an OAuth2AccessToken or if not token cached observable which throws NotTokenFoundException
     */
    public static Observable<com.github.scribejava.core.model.OAuth2AccessToken> getTokenOAuth2(final Class<? extends DefaultApi20> classApi) {
        return Observable.defer(new Func0<Observable<com.github.scribejava.core.model.OAuth2AccessToken>>() {
            @Override public Observable<com.github.scribejava.core.model.OAuth2AccessToken> call() {
                String keyToken = classApi.getSimpleName();

                Observable<OAuth2AccessToken> oToken = (Observable<OAuth2AccessToken>) TokenCache.INSTANCE.get(keyToken, OAuth2AccessToken.class);
                if (oToken != null) {
                    return oToken.map(new Func1<OAuth2AccessToken, com.github.scribejava.core.model.OAuth2AccessToken>() {
                        @Override public com.github.scribejava.core.model.OAuth2AccessToken call(OAuth2AccessToken token) {
                            return token.toOAuth2AccessTokenScribe();
                        }
                    });
                }

                return Observable.error(new NotActiveTokenFoundException());
            }
        });
    }

    private static final String ERROR_RETRIEVING_TOKEN = "Error retrieving token";
    private static <T extends Token> Observable<Response<Object, T>> startActivity(final Object targetUI, Service<T, ? extends OAuthService> service, final String keyToken, final Class<T> classToken) {
        Activity activity = targetUI instanceof Activity ? (Activity) targetUI : ((Fragment) targetUI).getActivity();

        Observable<T> response = (Observable<T>) TokenCache.INSTANCE.get(keyToken, classToken);
        if (response != null) return response.map(new Func1<T, Response<Object, T>>() {
            @Override public Response<Object, T> call(T token) {
                return new Response(targetUI, token);
            }
        });

        ActivityConnect.service = service;
        Intent intent = new Intent(activity, ActivityConnect.class);

        Observable oTempResponse;

        if (targetUI instanceof Activity) {
            oTempResponse = RxActivityResult.on((Activity) targetUI)
                    .startIntent(intent);
        } else {
            oTempResponse = RxActivityResult.on((Fragment) targetUI)
                    .startIntent(intent);
        }

        Observable<Result> oResponse = (Observable<Result>) oTempResponse;
        return oResponse.map(new Func1<Result, Response<Object, T>>() {
            @Override public Response<Object, T> call(Result result) {
                ActivityConnect.service = null;

                if (result.data() == null) throw new RuntimeException(ERROR_RETRIEVING_TOKEN);

                if (result.resultCode() == Activity.RESULT_OK) {
                    T token = (T) result.data().getExtras().getSerializable(ActivityConnect.KEY_RESULT);
                    TokenCache.INSTANCE.save(keyToken, token);
                    return new Response(targetUI, token);
                }

                String errorMessage = result.data().getExtras().getString(ActivityConnect.KEY_RESULT, "");
                if (!errorMessage.isEmpty()) throw new RuntimeException(errorMessage);
                else throw new RuntimeException(ERROR_RETRIEVING_TOKEN);
            }
        });
    }
}

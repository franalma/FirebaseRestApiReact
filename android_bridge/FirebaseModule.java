package com.dtse.fra.sp.test.react;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.firebase.rest.neli.auth.AccessTokenResponse;
import com.firebase.rest.neli.auth.FirebaseRestAuth;
import com.firebase.rest.neli.auth.LoginResponse;
import com.firebase.rest.neli.firestore.FireStoreRestApi;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class FirebaseModule extends ReactContextBaseJavaModule {

    FirebaseRestAuth firebaseRestAuth;
    FireStoreRestApi fireStoreRestApi;

    public FirebaseModule(ReactApplicationContext reactContext){
        super(reactContext);
    }

    @Override
    public String getName(){
        return "FirebaseRestApi";
    }

    @ReactMethod
    public void init(String projectId, String apiKey){
        this.firebaseRestAuth = new FirebaseRestAuth(apiKey);
        this.fireStoreRestApi = new FireStoreRestApi(getCurrentActivity(),
                projectId, this.firebaseRestAuth,"(default)");
    }

    @ReactMethod
    public String get(String path, Callback onResponse){
        this.fireStoreRestApi.get(path, new Continuation<JSONObject>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object result) {
                JSONObject joc = (JSONObject)result;
                onResponse.invoke(joc.toString());
            }
        });
        return "";
    }

    @ReactMethod
    public void set(String path, String value){
        try{
            fireStoreRestApi.set(path, new JSONObject(value), new Continuation<String>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object result) { }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @ReactMethod
    public void doLogin(String user, String pass, boolean returnSecureToken,
                        Callback onSuccess, Callback onError){
        Continuation<LoginResponse> continuation = new Continuation<LoginResponse>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object result) {
                if (result.getClass().getName().contains("kotlin.Result$Failure")) {
                    onError.invoke();
                }else{
                    LoginResponse response = (LoginResponse)result;
                    firebaseRestAuth.getAccessToken(response.getRefreshToken(), new Continuation<AccessTokenResponse>() {
                        @NotNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NotNull Object result) {
                            AccessTokenResponse accessTokenResponse = (AccessTokenResponse)result;
                            onSuccess.invoke(accessTokenResponse.getIdToken());
                        }
                    });

                }

            }
        };
        firebaseRestAuth.doLogin(user, pass,returnSecureToken, continuation);

    }


}

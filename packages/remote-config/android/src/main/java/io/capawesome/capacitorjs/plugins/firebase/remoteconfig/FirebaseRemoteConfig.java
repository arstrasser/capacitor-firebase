package io.capawesome.capacitorjs.plugins.firebase.remoteconfig;

import static io.capawesome.capacitorjs.plugins.firebase.remoteconfig.FirebaseRemoteConfigPlugin.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events.ConfigUpdateErrorEvent;
import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events.ConfigUpdateEvent;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRemoteConfig {

    private final FirebaseRemoteConfigPlugin plugin;
    private final com.google.firebase.remoteconfig.FirebaseRemoteConfig remoteConfigInstance;

    @Nullable
    private ConfigUpdateListenerRegistration configUpdateListenerRegistration;
    private int configUpdateListenerRegistrationCount = 0;

    public FirebaseRemoteConfig(FirebaseRemoteConfigPlugin plugin) {
        this.plugin = plugin;
        this.remoteConfigInstance = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
    }

    public void activate(final ActivateResultCallback resultCallback) {
        remoteConfigInstance
            .activate()
            .addOnSuccessListener(
                result -> {
                    resultCallback.success(result.booleanValue());
                }
            )
            .addOnFailureListener(
                exception -> {
                    Log.w(TAG, "Activate config failed.", exception);
                    resultCallback.error(exception.getMessage());
                }
            );
    }

    public void fetchAndActivate(final ActivateResultCallback resultCallback) {
        remoteConfigInstance
            .fetchAndActivate()
            .addOnSuccessListener(
                result -> {
                    resultCallback.success(result.booleanValue());
                }
            )
            .addOnFailureListener(
                exception -> {
                    Log.w(TAG, "Fetch and activate config failed.", exception);
                    resultCallback.error(exception.getMessage());
                }
            );
    }

    public void fetchConfig(long minimumFetchIntervalInSeconds, final FetchConfigResultCallback resultCallback) {
        remoteConfigInstance
            .fetch(minimumFetchIntervalInSeconds)
            .addOnSuccessListener(
                result -> {
                    resultCallback.success();
                }
            )
            .addOnFailureListener(
                exception -> {
                    Log.w(TAG, "Fetch config failed.", exception);
                    resultCallback.error(exception.getMessage());
                }
            );
    }

    public GetValueResult<Boolean> getBoolean(String key) {
        FirebaseRemoteConfigValue value = remoteConfigInstance.getValue(key);
        return new GetValueResult<Boolean>(value.asBoolean(), value.getSource());
    }

    public GetValueResult<Double> getNumber(String key) {
        FirebaseRemoteConfigValue value = remoteConfigInstance.getValue(key);
        return new GetValueResult<Double>(value.asDouble(), value.getSource());
    }

    public GetValueResult<String> getString(String key) {
        FirebaseRemoteConfigValue value = remoteConfigInstance.getValue(key);
        return new GetValueResult<String>(value.asString(), value.getSource());
    }

    public void addConfigUpdateListener() {
        this.configUpdateListenerRegistrationCount++;
        if (this.configUpdateListenerRegistration != null) {
            return;
        }
        this.configUpdateListenerRegistration =
                this.remoteConfigInstance.addOnConfigUpdateListener(
                        new ConfigUpdateListener() {
                            @Override
                            public void onUpdate(ConfigUpdate configUpdate) {
                                ConfigUpdateEvent event = new ConfigUpdateEvent(
                                        configUpdate
                                );
                                plugin.notifyConfigUpdateEventListener(event);
                            }

                            @Override
                            public void onError(FirebaseRemoteConfigException error) {
                                ConfigUpdateErrorEvent event = new ConfigUpdateErrorEvent(
                                        error
                                );
                                plugin.notifyConfigUpdateErrorEventListener(event);
                            }
                        }
                );
    }

    public void removeConfigUpdateListener() {
        if (this.configUpdateListenerRegistrationCount > 0) {
            this.configUpdateListenerRegistrationCount--;
        }
        if (this.configUpdateListenerRegistrationCount == 0) {
            this.configUpdateListenerRegistration.remove();
            this.configUpdateListenerRegistration = null;
        }
    }

    public void removeAllListeners() {
        this.configUpdateListenerRegistrationCount = 0;
        this.configUpdateListenerRegistration.remove();
        this.configUpdateListenerRegistration = null;
    }
}

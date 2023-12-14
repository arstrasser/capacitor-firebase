package io.capawesome.capacitorjs.plugins.firebase.remoteconfig;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events.ConfigUpdateErrorEvent;
import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events.ConfigUpdateEvent;

@CapacitorPlugin(name = "FirebaseRemoteConfig")
public class FirebaseRemoteConfigPlugin extends Plugin {

    public static final String TAG = "FirebaseRemoteConfig";
    public static final String EVENT_NAME_CONFIG_UPDATE = "configUpdate";
    public static final String EVENT_NAME_CONFIG_UPDATE_ERROR = "configUpdateError";
    public static final String ERROR_KEY_MISSING = "key must be provided.";

    private FirebaseRemoteConfig implementation;

    public void load() {
        implementation = new FirebaseRemoteConfig(this);
    }

    @PluginMethod
    public void activate(PluginCall call) {
        try {
            implementation.activate(
                new ActivateResultCallback() {
                    @Override
                    public void success(boolean success) {
                        call.resolve();
                    }

                    @Override
                    public void error(String message) {
                        call.reject(message);
                    }
                }
            );
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void fetchAndActivate(PluginCall call) {
        try {
            implementation.fetchAndActivate(
                new ActivateResultCallback() {
                    @Override
                    public void success(boolean success) {
                        call.resolve();
                    }

                    @Override
                    public void error(String message) {
                        call.reject(message);
                    }
                }
            );
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void fetchConfig(PluginCall call) {
        try {
            int minimumFetchIntervalInSeconds = call.getInt("minimumFetchIntervalInSeconds", 43200);
            implementation.fetchConfig(
                minimumFetchIntervalInSeconds,
                new FetchConfigResultCallback() {
                    @Override
                    public void success() {
                        call.resolve();
                    }

                    @Override
                    public void error(String message) {
                        call.reject(message);
                    }
                }
            );
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void getBoolean(PluginCall call) {
        try {
            String key = call.getString("key");
            if (key == null) {
                call.reject(ERROR_KEY_MISSING);
                return;
            }
            GetValueResult<Boolean> getValueResult = implementation.getBoolean(key);
            JSObject result = new JSObject();
            result.put("value", getValueResult.value);
            result.put("source", getValueResult.source);
            call.resolve(result);
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void getNumber(PluginCall call) {
        try {
            String key = call.getString("key");
            if (key == null) {
                call.reject(ERROR_KEY_MISSING);
                return;
            }
            GetValueResult<Double> getValueResult = implementation.getNumber(key);
            JSObject result = new JSObject();
            result.put("value", getValueResult.value);
            result.put("source", getValueResult.source);
            call.resolve(result);
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void getString(PluginCall call) {
        try {
            String key = call.getString("key");
            if (key == null) {
                call.reject(ERROR_KEY_MISSING);
                return;
            }
            GetValueResult<String> getValueResult = implementation.getString(key);
            JSObject result = new JSObject();
            result.put("value", getValueResult.value);
            result.put("source", getValueResult.source);
            call.resolve(result);
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void setMinimumFetchInterval(PluginCall call) {
        call.reject("Not available on Android.");
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void addListener(PluginCall call) {
        String eventName = call.getString("eventName");

        if (eventName.equals(EVENT_NAME_CONFIG_UPDATE)) {
            implementation.addConfigUpdateListener();
        }

        super.addListener(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void removeListener(PluginCall call) {
        String eventName = call.getString("eventName");

        if (eventName.equals(EVENT_NAME_CONFIG_UPDATE)) {
            implementation.removeConfigUpdateListener();
        }

        super.removeListener(call);
    }

    @PluginMethod
    public void removeAllListeners(PluginCall call) {
        try {
            implementation.removeAllListeners();
            super.removeAllListeners(call);
        } catch (Exception exception) {
            Logger.error(TAG, exception.getMessage(), exception);
            call.reject(exception.getMessage());
        }
    }

    public void notifyConfigUpdateEventListener(@NonNull ConfigUpdateEvent event) {
        notifyListeners(EVENT_NAME_CONFIG_UPDATE, event.toJSObject());
    }

    public void notifyConfigUpdateErrorEventListener(@NonNull ConfigUpdateErrorEvent event) {
        notifyListeners(EVENT_NAME_CONFIG_UPDATE, event.toJSObject());
    }
}

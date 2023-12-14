package io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;

import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.interfaces.Result;

public class ConfigUpdateErrorEvent implements Result {

    private FirebaseRemoteConfigException exception;

    public ConfigUpdateErrorEvent(FirebaseRemoteConfigException exception) {
        this.exception = exception;
    }

    public JSObject toJSObject() {
        JSObject result = new JSObject();
        result.put("message", exception.getMessage());
        return result;
    }
}

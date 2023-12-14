package io.capawesome.capacitorjs.plugins.firebase.remoteconfig.classes.events;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.google.firebase.remoteconfig.ConfigUpdate;
import io.capawesome.capacitorjs.plugins.firebase.remoteconfig.interfaces.Result;

public class ConfigUpdateEvent implements Result {

    private ConfigUpdate configUpdate;

    public ConfigUpdateEvent(ConfigUpdate configUpdate) {
        this.configUpdate = configUpdate;
    }

    public JSObject toJSObject() {
        JSArray updatedKeysResult = new JSArray();
        for (String key : configUpdate.getUpdatedKeys()) {
            updatedKeysResult.put(key);
        }

        JSObject result = new JSObject();
        result.put("updatedKeys", updatedKeysResult);
        return result;
    }
}

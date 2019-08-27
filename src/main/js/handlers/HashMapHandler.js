"use strict";
class HashMapHandler {
    
    static jjjIsTransient() {
        return false;
    }

    static jjjGetClass() {
        return "java.util.HashMap";
    }
    
    jjjConstructor(){
        return new HashMap();
    }
    
    jjjDecode(resObj) {
        let keys = null;
        let values = null;

        let cb1 = function (r) {
            keys = r;
            resObj.decodeField("values", cb2);
        };

        let cb2 = function (r) {
            values = r;
            for (let i = 0; i < keys.length; i++) {
                this.put(keys[i], values[i]);
            }
        }.bind(this);

        resObj.decodeField("keys", cb1);
    }

    jjjEncode(encodedObject) {
        let keys = [];
        let values = [];

        this.map.forEach((value, key)=>{
            keys.push(key);
            values.push(value);
        });

        encodedObject.setField("keys", keys);
        encodedObject.setField("values", values);
    }
};

module.exports = HashMap;
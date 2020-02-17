"use strict";
const Modifier = require("./Modifier");
const Object = require("./Object");
const Constants = require("./Constants");
const Encoder = require("./Encoder");
const Transient = require("./Transient");
const JSONObject = require("./JSONObject");
const JJJObject = require("./JJJObject");
class EncodedObject {
	constructor(object, encodedResult) {
            /* no super class */;
            this.object = object;
            this.json = new JSONObject();
            this.encodedResult = encodedResult;
            this.json.put(Constants.KeyParam, encodedResult.getTranslator().allocReference(object));
            this.json.put(Constants.TypeParam, object.getClass().getName());
            this.json.put(Constants.FieldsParam, new JSONObject());
        }
	encode() {
            EncodedObject.LOGGER.trace("EncodedObject.encode() : " + this.object.getClass().getSimpleName());
            /* encode all fields for each class and superclass until JJJObject or Object is reached */;
            let aClass = this.object.getClass();
            while(aClass !== JJJObject.class && aClass !== Object.class){
                for(let field of aClass.getDeclaredFields()){
                    this.setField(field);
                }
                aClass = aClass.getSuperclass();
            }
            this.encodedResult.getTranslator().notifyEncode(this.object);
        }
	setField(field) {
            field.setAccessible(true);
            if (field.getAnnotation(Transient.class) !== null)return ;
            
            if (Modifier.isStatic(field.getModifiers()))return ;
            
            let toJSON = new Encoder(field.get(this.object), this.encodedResult).encode();
            this.setFieldData(field.getName(), toJSON);
        }
	setFieldData(name, json) {
            this.json.getJSONObject(Constants.FieldsParam).put(name, json);
        }
};
EncodedObject.LOGGER = org.apache.logging.log4j.LogManager.getLogger("JSONObject");

if (typeof module !== "undefined") module.exports = EncodedObject;
// Generated by JJJRMI 0.6.3
// 20.02.16 18:06:58

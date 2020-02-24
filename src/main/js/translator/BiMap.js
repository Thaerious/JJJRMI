"use strict";

class BiMap {
    constructor() {
        this.objectMap = new Map();
        this.reverseMap = new Map();
    }
    size(){
        return this.objectMap.size;
    }
    clear() {
        this.objectMap.clear();
        this.reverseMap.clear();
    }
    keys() {
        return this.objectMap.keys();
    }
    removeByKey(key) {
        let obj = this.objectMap.get(key);
        this.objectMap.delete(key);
        this.reverseMap.delete(obj);
    }
    removeByValue(obj) {
        let key = this.reverseMap.get(obj);
        this.objectMap.delete(key);
        this.reverseMap.delete(obj);
    }
    get(key) {
        return this.objectMap.get(key);
    }
    put(key, value) {
        this.objectMap.set(key, value);
        this.reverseMap.set(value, key);
    }
    getKey(value) {
        return this.reverseMap.get(value);
    }
    containsKey(key) {
        return this.objectMap.has(key);
    }
    containsValue(value) {
        return this.reverseMap.has(value);
    }
    /* remove target freom the translator replacing it with source, maintaining the same key */
    swap(source, target) {
        let key = this.getKey(target);
        this.objectMap.set(key, source);
        this.reverseMap.delete(target);
        this.reverseMap.set(source, key);
    }
}

module.exports = BiMap;
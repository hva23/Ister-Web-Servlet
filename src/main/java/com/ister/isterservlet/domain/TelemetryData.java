package com.ister.isterservlet.domain;

import java.util.HashMap;
import java.util.Map;

public class TelemetryData extends BaseEntity<Long> {
    private Map<String, Object> data;
    private Product product;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(String key, Object value){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(key, value);
        this.data = dataMap;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

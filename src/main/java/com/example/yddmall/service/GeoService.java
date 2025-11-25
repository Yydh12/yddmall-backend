package com.example.yddmall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GeoService {

    // 高德地图配置
    @Value("${geo.amap.key:}")
    private String amapKey;

    @Value("${geo.amap.base-url:https://restapi.amap.com/v3}")
    private String amapBaseUrl;

    @Value("${geo.amap.min-interval-ms:100}")
    private long amapMinIntervalMs;

    // 天地图配置
    // 优先使用服务端Key；未配置则回退到旧的 key（兼容）
    @Value("${geo.tianditu.server-key:${geo.tianditu.key:}}")
    private String tiandituKey;

    @Value("${geo.tianditu.base-url:http://api.tianditu.gov.cn}")
    private String tiandituBaseUrl;

    @Value("${geo.tianditu.min-interval-ms:150}")
    private long tiandituMinIntervalMs;

    // Nominatim 配置（备用）
    @Value("${geo.nominatim.base-url:https://nominatim.openstreetmap.org}")
    private String nominatimBaseUrl;

    @Value("${geo.nominatim.user-agent:yddmall/1.0 (dev)}")
    private String userAgent;

    @Value("${geo.nominatim.min-interval-ms:1100}")
    private long nominatimMinIntervalMs;

    private final AtomicLong lastAmapRequestAt = new AtomicLong(0);
    private final AtomicLong lastTiandituRequestAt = new AtomicLong(0);
    private final AtomicLong lastNominatimRequestAt = new AtomicLong(0);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeoService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3s 连接超时
        factory.setReadTimeout(4000);    // 4s 读取超时
        this.restTemplate = new RestTemplate(factory);
    }

    private void rateLimitAmap() {
        long prev = lastAmapRequestAt.get();
        long now = System.currentTimeMillis();
        long wait = prev + amapMinIntervalMs - now;
        if (wait > 0) {
            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
        }
        lastAmapRequestAt.set(System.currentTimeMillis());
    }

    private void rateLimitTianditu() {
        long prev = lastTiandituRequestAt.get();
        long now = System.currentTimeMillis();
        long wait = prev + tiandituMinIntervalMs - now;
        if (wait > 0) {
            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
        }
        lastTiandituRequestAt.set(System.currentTimeMillis());
    }
    private void rateLimitNominatim() {
        long prev = lastNominatimRequestAt.get();
        long now = System.currentTimeMillis();
        long wait = prev + nominatimMinIntervalMs - now;
        if (wait > 0) {
            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
        }
        lastNominatimRequestAt.set(System.currentTimeMillis());
    }

    public String search(String q, Integer limit) {
        // 仅使用天地图搜索
        if (tiandituKey != null && !tiandituKey.isEmpty()) {
            try {
                return searchWithTianditu(q, limit);
            } catch (Exception e) {
                System.err.println("天地图搜索失败: " + e.getMessage());
            }
        }
        // 未配置天地图 Key 时返回空结果
        return "[]";
    }

    private String searchWithAmap(String q, Integer limit) throws Exception {
        rateLimitAmap();
        String qs = "key=" + amapKey + 
                   "&keywords=" + URLEncoder.encode(q, StandardCharsets.UTF_8) +
                   "&output=json" +
                   "&offset=" + (limit == null ? 10 : limit);
        URI uri = URI.create(amapBaseUrl + "/place/text?" + qs);
        
        ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
        String amapResult = resp.getBody();
        
        // 转换高德地图结果为Nominatim格式
        return convertAmapToNominatimFormat(amapResult);
    }

    private String searchWithTianditu(String q, Integer limit) throws Exception {
        rateLimitTianditu();
        int count = (limit == null ? 10 : limit);
        // 使用全球范围与地名搜索类型，提高命中率
        String postStr = "{" +
                "\"keyWord\":\"" + q.replace("\"", " ") + "\"," +
                "\"level\":\"12\"," +
                "\"mapBound\":\"-180,-90,180,90\"," +
                "\"queryType\":\"7\"," +
                "\"start\":\"0\"," +
                "\"count\":\"" + count + "\"" +
                "}";
        String url = tiandituBaseUrl + "/v2/search?postStr=" + URLEncoder.encode(postStr, StandardCharsets.UTF_8) + "&type=query&tk=" + tiandituKey;
        URI uri = URI.create(url);
        ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
        String tdResult = resp.getBody();
        System.out.println("[Tianditu search] uri=" + uri + ", raw=" + (tdResult == null ? "null" : tdResult.substring(0, Math.min(tdResult.length(), 200))));
        return convertTiandituToNominatimFormat(tdResult);
    }

    // 不再使用 Nominatim 搜索，保留方法以备参考（未调用）

    private String convertAmapToNominatimFormat(String amapResult) throws Exception {
        JsonNode amapJson = objectMapper.readTree(amapResult);
        JsonNode pois = amapJson.get("pois");
        
        if (pois == null || !pois.isArray()) {
            return "[]";
        }

        StringBuilder result = new StringBuilder("[");
        boolean first = true;
        
        for (JsonNode poi : pois) {
            if (!first) result.append(",");
            first = false;
            
            String name = poi.get("name").asText("");
            String address = poi.get("address").asText("");
            String location = poi.get("location").asText("");
            String[] coords = location.split(",");
            
            if (coords.length == 2) {
                double lng = Double.parseDouble(coords[0]);
                double lat = Double.parseDouble(coords[1]);
                
                result.append("{")
                      .append("\"display_name\":\"").append(name).append(" - ").append(address).append("\",")
                      .append("\"lat\":\"").append(lat).append("\",")
                      .append("\"lon\":\"").append(lng).append("\"")
                      .append("}");
            }
        }
        
        result.append("]");
        return result.toString();
    }

    public String reverse(double lat, double lon) {
        // 仅使用天地图逆地理编码
        if (tiandituKey != null && !tiandituKey.isEmpty()) {
            try {
                return reverseWithTianditu(lat, lon);
            } catch (Exception e) {
                System.err.println("天地图逆地理编码失败: " + e.getMessage());
            }
        }
        // 未配置天地图 Key 时返回空对象
        return "{}";
    }

    private String reverseWithAmap(double lat, double lon) throws Exception {
        rateLimitAmap();
        String qs = "key=" + amapKey + 
                   "&location=" + lon + "," + lat +
                   "&output=json";
        URI uri = URI.create(amapBaseUrl + "/geocode/regeo?" + qs);
        
        ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
        String amapResult = resp.getBody();
        
        // 转换高德地图结果为Nominatim格式
        return convertAmapReverseToNominatimFormat(amapResult, lat, lon);
    }

    private String reverseWithTianditu(double lat, double lon) throws Exception {
        rateLimitTianditu();
        String postStr = "{" +
                "\"lon\":" + lon + "," +
                "\"lat\":" + lat + "," +
                "\"ver\":1" +
                "}";
        String url = tiandituBaseUrl + "/geocoder?postStr=" + URLEncoder.encode(postStr, StandardCharsets.UTF_8) + "&type=geocode&tk=" + tiandituKey;
        URI uri = URI.create(url);
        ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
        String tdResult = resp.getBody();
        System.out.println("[Tianditu reverse] uri=" + uri + ", raw=" + (tdResult == null ? "null" : tdResult.substring(0, Math.min(tdResult.length(), 200))));
        return convertTiandituReverseToNominatimFormat(tdResult, lat, lon);
    }

    // 不再使用 Nominatim 逆地理编码，保留方法以备参考（未调用）

    private String convertAmapReverseToNominatimFormat(String amapResult, double lat, double lon) throws Exception {
        JsonNode amapJson = objectMapper.readTree(amapResult);
        JsonNode regeocode = amapJson.get("regeocode");
        
        if (regeocode == null) {
            return "{}";
        }

        JsonNode addressComponent = regeocode.get("addressComponent");
        String formattedAddress = regeocode.get("formatted_address").asText("");
        
        StringBuilder result = new StringBuilder("{");
        result.append("\"display_name\":\"").append(formattedAddress).append("\",");
        result.append("\"lat\":\"").append(lat).append("\",");
        result.append("\"lon\":\"").append(lon).append("\"");
        
        if (addressComponent != null) {
            result.append(",\"address\":{");
            result.append("\"country\":\"").append(addressComponent.get("country").asText("")).append("\",");
            result.append("\"state\":\"").append(addressComponent.get("province").asText("")).append("\",");
            result.append("\"city\":\"").append(addressComponent.get("city").asText("")).append("\",");
            result.append("\"district\":\"").append(addressComponent.get("district").asText("")).append("\"");
            result.append("}");
        }
        
        result.append("}");
        return result.toString();
    }

    private String convertTiandituToNominatimFormat(String tdResult) throws Exception {
        JsonNode tdJson = objectMapper.readTree(tdResult);
        JsonNode pois = tdJson.get("pois");
        if (pois == null || !pois.isArray()) {
            return "[]";
        }
        StringBuilder result = new StringBuilder("[");
        boolean first = true;
        for (JsonNode poi : pois) {
            if (!first) result.append(",");
            first = false;
            String name = poi.path("name").asText("");
            String address = poi.path("address").asText("");
            String lonlat = poi.path("lonlat").asText("");
            String[] coords = lonlat.split(",");
            if (coords.length == 2) {
                double lng = Double.parseDouble(coords[0]);
                double lat = Double.parseDouble(coords[1]);
                result.append("{")
                      .append("\"display_name\":\"").append(name).append(" - ").append(address).append("\",")
                      .append("\"lat\":\"").append(lat).append("\",")
                      .append("\"lon\":\"").append(lng).append("\"")
                      .append("}");
            }
        }
        result.append("]");
        return result.toString();
    }

    private String convertTiandituReverseToNominatimFormat(String tdResult, double lat, double lon) throws Exception {
        JsonNode tdJson = objectMapper.readTree(tdResult);
        JsonNode resultNode = tdJson.path("result");
        if (resultNode.isMissingNode()) {
            return "{}";
        }
        String formattedAddress = resultNode.path("formatted_address").asText("");
        JsonNode comp = resultNode.path("addressComponent");
        String country = comp.path("country").asText("");
        if (country.isEmpty()) country = "中国";
        String province = comp.path("province").asText("");
        String city = comp.path("city").asText("");
        String district = comp.path("district").asText("");
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"display_name\":\"").append(formattedAddress).append("\",");
        sb.append("\"lat\":\"").append(lat).append("\",");
        sb.append("\"lon\":\"").append(lon).append("\"");
        sb.append(",\"address\":{");
        sb.append("\"country\":\"").append(country).append("\",");
        sb.append("\"state\":\"").append(province).append("\",");
        sb.append("\"city\":\"").append(city).append("\",");
        sb.append("\"district\":\"").append(district).append("\"}");
        sb.append("}");
        return sb.toString();
    }
}
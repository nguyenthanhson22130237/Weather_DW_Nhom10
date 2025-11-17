package vn.edu.hcmuaf.fit.web.ControlManager;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;

public class ConfigManager {
    private String apiUrl;
    private String apiKey;
    private String[] cities;
    private String stagingUrl;
    private String warehouseUrl;
    private String logUrl;
    private String dbUserCommon;
    private String dbPasswordCommon;
    private int days;
    private String aqi;
    private String alerts;


    public ConfigManager(String filePath) {
        loadConfig(filePath);
    }

    private void loadConfig(String configFile) {
        try {
//           //Đọc file config.xml
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile);
            if (inputStream == null) {
                throw new RuntimeException("Không tìm thấy file cấu hình: " + configFile);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Đọc phần <api>
            apiKey = doc.getElementsByTagName("apiKey").item(0).getTextContent();
            apiUrl = doc.getElementsByTagName("apiUrl").item(0).getTextContent();
            days = Integer.parseInt(doc.getElementsByTagName("days").item(0).getTextContent());
            aqi = doc.getElementsByTagName("aqi").item(0).getTextContent();
            alerts = doc.getElementsByTagName("alerts").item(0).getTextContent();

            NodeList cityNodes = doc.getElementsByTagName("city");
            cities = new String[cityNodes.getLength()];
            for (int i = 0; i < cityNodes.getLength(); i++) {
                cities[i] = cityNodes.item(i).getTextContent();
            }

            Element common = (Element) doc.getElementsByTagName("database_common_credentials").item(0);
            this.dbUserCommon = common.getElementsByTagName("username").item(0).getTextContent();
            this.dbPasswordCommon = common.getElementsByTagName("password").item(0).getTextContent();

            // Lấy thông tin DB staging
            Element staging = (Element) doc.getElementsByTagName("database_staging").item(0);
            this.stagingUrl = staging.getElementsByTagName("url").item(0).getTextContent();

            // Lấy thông tin DB wh
            Element wh = (Element) doc.getElementsByTagName("database_warehouse").item(0);
            this.warehouseUrl = wh.getElementsByTagName("url").item(0).getTextContent();

            // Lấy URL DB log (Thêm vào)
            Element log = (Element) doc.getElementsByTagName("database_log").item(0);
            this.logUrl = log.getElementsByTagName("url").item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getters
    public String getApiUrl() { return apiUrl; }
    public String getApiKey() { return apiKey; }
    public String[] getCities() { return cities; }
    public String getStagingUrl() { return stagingUrl; }
    public String getWarehouseUrl() { return warehouseUrl; }

    public String getDbUserCommon() {
        return dbUserCommon;
    }

    public String getLogUrl() {
        return logUrl;
    }

    public String getDbPasswordCommon() {
        return dbPasswordCommon;
    }

    public int getDays() {
        return days;
    }

    public String getAqi() {
        return aqi;
    }

    public String getAlerts() {
        return alerts;
    }
}

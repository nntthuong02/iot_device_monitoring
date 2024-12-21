#include <DHT11.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

// Định nghĩa các chân GPIO
const int DEN1 = 2;  // Chân GPIO kết nối đèn LED
const int DEN2 = 4;
const int DEN3 = 18;
const int DENSAN = 5;
const int PIR = 14;

// Thông tin Wi-Fi
const char* ssid = "Wifi 5G";
const char* password = "123123123";

// Địa chỉ server backend
const char* backend_url = "http://192.168.107.122:8000";

// Cảm biến DHT11
DHT11 dht11(26);

void setup() {
  // Cấu hình các chân đầu ra
  pinMode(DEN1, OUTPUT);
  pinMode(DEN2, OUTPUT);
  pinMode(DEN3, OUTPUT);
  pinMode(DENSAN, OUTPUT);
  pinMode(PIR, INPUT);

  // Khởi tạo Serial
  Serial.begin(115200);

  // Kết nối Wi-Fi
  Serial.println("\nĐang kết nối Wi-Fi...");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWi-Fi đã kết nối");
  Serial.print("Địa chỉ IP: ");
  Serial.println(WiFi.localIP()); // Hiển thị địa chỉ IP của ESP32
}

void loop() {
  // Kiểm tra kết nối Wi-Fi
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    // Lấy trạng thái đèn từ backend
    String url = String(backend_url) + "/get";
    Serial.print("Gửi yêu cầu GET tới URL: ");
    Serial.println(url);

    http.begin(url);
    int httpCode = http.GET();

    if (httpCode < 0) {
      Serial.print("HTTP Code nhận được: ");
      Serial.println(httpCode);
      
      if (httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println("Nhận dữ liệu từ server:");
        Serial.println(payload);

        // Phân tích dữ liệu JSON
        StaticJsonDocument<200> doc;
        DeserializationError error = deserializeJson(doc, payload);
        if (error) {
          Serial.print("Lỗi phân tích JSON: ");
          Serial.println(error.c_str());
        } else {
          int den1_status = doc["Den1"];
          int den2_status = doc["Den2"];
          int den3_status = doc["Den3"]["TrangThai"];
          int gio_hen = doc["Den3"]["Gio"];
          int phut_hen = doc["Den3"]["Phut"];

          Serial.print("Trạng thái đèn 1: ");
          Serial.println(den1_status);
          Serial.print("Trạng thái đèn 2: ");
          Serial.println(den2_status);
          Serial.print("Trạng thái đèn 3: ");
          Serial.println(den3_status);

          // Điều khiển đèn
          digitalWrite(DEN1, den1_status ? HIGH : LOW);
          digitalWrite(DEN2, den2_status ? HIGH : LOW);
          digitalWrite(DEN3, den3_status ? HIGH : LOW);

          // Hẹn giờ tắt đèn 3
          if (den3_status) {
            struct tm timeinfo;
            if (getLocalTime(&timeinfo)) {
              int current_hour = timeinfo.tm_hour;
              int current_minute = timeinfo.tm_min;
              Serial.print("Giờ hiện tại: ");
              Serial.print(current_hour);
              Serial.print(":");
              Serial.println(current_minute);

              if (gio_hen == current_hour && phut_hen == current_minute) {
                String off_url = String(backend_url) + "/setDen3Off";
                Serial.print("Gửi yêu cầu tắt đèn 3 tới URL: ");
                Serial.println(off_url);
                http.begin(off_url);
                int offCode = http.GET();
                Serial.print("HTTP Code tắt đèn 3: ");
                Serial.println(offCode);
              }
            } else {
              Serial.println("Không thể lấy giờ hệ thống.");
            }
          }
        }
      }
    } else {
      Serial.print("Không thể kết nối server. Lỗi: ");
      Serial.println(http.errorToString(httpCode).c_str());
    }
    http.end();

    // Gửi dữ liệu cảm biến
    int humidity = dht11.readHumidity();
    int temperature = dht11.readTemperature();
    Serial.print("Độ ẩm: ");
    Serial.println(humidity);
    Serial.print("Nhiệt độ: ");
    Serial.println(temperature);

    String data_url = String(backend_url) + "/updateSensor";
    Serial.print("Gửi dữ liệu cảm biến tới URL: ");
    Serial.println(data_url);

    http.begin(data_url);
    http.addHeader("Content-Type", "application/json");
    String jsonPayload = "{\"DoAm\":" + String(humidity) + ",\"NhietDo\":" + String(temperature) + "}";
    int sensorCode = http.POST(jsonPayload);
    Serial.print("HTTP Code dữ liệu cảm biến: ");
    Serial.println(sensorCode);
    http.end();

    // Cảm biến chuyển động
    int pir_value = digitalRead(PIR);
    Serial.print("Trạng thái cảm biến chuyển động: ");
    Serial.println(pir_value);

    String pir_url = String(backend_url) + "/updateMotion";
    Serial.print("Gửi trạng thái cảm biến chuyển động tới URL: ");
    Serial.println(pir_url);

    http.begin(pir_url);
    http.addHeader("Content-Type", "application/json");
    String pirPayload = "{\"Densan\":" + String(pir_value) + "}";
    int pirCode = http.POST(pirPayload);
    Serial.print("HTTP Code cảm biến chuyển động: ");
    Serial.println(pirCode);
    http.end();
  } else {
    Serial.println("Wi-Fi không kết nối. Đang thử lại...");
    WiFi.reconnect();
  }

  delay(1000); // Thêm độ trễ để giảm tải server
}

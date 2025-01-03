#include <WiFi.h>
#include <HTTPClient.h>

// Thông tin Wi-Fi
const char* ssid = "Wifi 5G";
const char* password = "123123123";

// Địa chỉ server
const char* serverIP = "192.168.212.68";
const int serverPort = 8000; // Cổng server

void setup() {
  // Khởi tạo Serial để debug
  Serial.begin(115200);

  // Kết nối Wi-Fi
  Serial.print("Đang kết nối tới Wi-Fi: ");
  Serial.println(ssid);
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
  if (WiFi.status() == WL_CONNECTED) { // Kiểm tra trạng thái Wi-Fi
    HTTPClient http;

    // Tạo URL để gửi yêu cầu
    String serverUrl = "http://" + String(serverIP) + ":" + String(serverPort) + "/get";
    Serial.print("Gửi yêu cầu GET đến: ");
    Serial.println(serverUrl);

    // Bắt đầu kết nối
    http.begin(serverUrl); 

    // Gửi yêu cầu GET
    int httpResponseCode = http.GET();

    // Kiểm tra kết quả phản hồi
    if (httpResponseCode > 0) {
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
      String response = http.getString();
      Serial.println("Phản hồi từ server:");
      Serial.println(response);
    } else {
      Serial.print("Lỗi gửi yêu cầu, mã lỗi: ");
      Serial.println(httpResponseCode);
    }

    // Đóng kết nối
    http.end();
  } else {
    Serial.println("Wi-Fi mất kết nối. Đang thử lại...");
    WiFi.reconnect();
  }

  delay(5000); // Chờ 5 giây trước khi gửi yêu cầu tiếp theo
}

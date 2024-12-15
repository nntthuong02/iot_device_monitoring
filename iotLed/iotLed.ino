#include <DHT11.h>
#include <ESP32Firebase.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
const int DEN1 = 2;  // Chân GPIO kết nối đèn LED
const int DEN2 = 4;
const int DEN3 = 18;
const int DENSAN = 5;
const int pir = 14;
const char* ssid = "lmao";
const char* password = "Kmk-1223334444";
#define REFERENCE_URL "https://iotled-cc921-default-rtdb.firebaseio.com/"

const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 7 * 3600;
const int daylightOffset_sec = 0;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, ntpServer, gmtOffset_sec, daylightOffset_sec);


Firebase firebase(REFERENCE_URL);

DHT11 dht11(26);

void setup() {
  pinMode(DEN1, OUTPUT);
  pinMode(DEN2, OUTPUT);
  pinMode(DEN3, OUTPUT);
  pinMode(DENSAN, OUTPUT);
  pinMode(pir, INPUT);
  Serial.begin(115200);

  // pinMode(LED_BUILTIN, OUTPUT);
  // digitalWrite(LED_BUILTIN, LOW);
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(1000);

  // Connect to WiFi
  Serial.println();
  Serial.println();
  Serial.print("Connecting to: ");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("-");
  }

  Serial.println("WiFi Connected");

  timeClient.begin();

}

void loop() {
// Lấy trạng thái đèn từ firebase
  int data_den1 = firebase.getInt("/Den1");
  int data_den2 = firebase.getInt("/Den2");
  int data_den3 = firebase.getInt("/Den3/TrangThai");
// Bật tắt đèn 1
  if(data_den1 == 1) {
    digitalWrite(DEN1, HIGH);
    Serial.println("Đèn 1 bật");
  }
  else {
    digitalWrite(DEN1, LOW);
    Serial.println("Đèn 1 tắt");
  } 
//Bật tắt đèn 2
  if(data_den2 == 1) {
    digitalWrite(DEN2, HIGH);
    Serial.println("Đèn 2 bật");
  }
  else {
    digitalWrite(DEN2, LOW);
    Serial.println("Đèn 2 tắt");
  } 
// bật tắt đèn 3
  if(data_den3 == 1) {
    digitalWrite(DEN3, HIGH);
    Serial.println("Đèn 3 bật");
    // Hẹn giờ đèn 3
    int gio = firebase.getInt("/Den3/Gio");
    int phut = firebase.getInt("/Den3/Phut");
    

    timeClient.update();
    // Lấy số giờ và số phút từ thời gian
    int hours = timeClient.getHours();
    int minutes = timeClient.getMinutes();

    // In thời gian lên Serial Monitor
    Serial.print("Giờ: ");
    Serial.print(hours);
    Serial.print(", Phút: ");
    Serial.println(minutes);

  //Hẹn giờ
    if (gio == hours && phut == minutes) {
      firebase.setInt("/Den3/TrangThai", 0);
      digitalWrite(DEN3, LOW);
    }
  }
  else {
    digitalWrite(DEN3, LOW);
    Serial.println("Đèn 3 tắt");
  } 
//Nhiệt đô, độ ẩm
  int humidity = dht11.readHumidity();
  int temperature = dht11.readTemperature();

  firebase.setInt("DoAm", humidity);
  firebase.setInt("NhietDo", temperature);

  Serial.print("Độ ẩm: ");
  Serial.print(humidity);
  Serial.print(" %\tNhiệt độ: asssss");
  Serial.print(temperature);
  Serial.println(" *C");

// Cảm biến chuyển động
  int pir_value = digitalRead(pir);
  if (pir_value == 1) {
    digitalWrite(DENSAN, HIGH);
    firebase.setInt("/Densan", 1);
    Serial.println("Phát hiện chuyển động");
  }
  else {
    digitalWrite(DENSAN, LOW);
    firebase.setInt("/Densan", 0);
    Serial.println("Không có chuyển động");
  }
}
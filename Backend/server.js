require("dotenv").config();
// Import thư viện cần thiết
const express = require("express");
const mongoose = require("mongoose");
const bodyParser = require("body-parser");
const cors = require("cors");

// Kết nối MongoDB sử dụng MongoDB Atlas (Cloud)
const uri = process.env.MONGO_URI;
mongoose.connect(uri, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

// Khởi tạo ứng dụng
const app = express();
app.use(bodyParser.json());
app.use(cors());

// Schema cho đèn 1, 2, 3
const LightSchema = new mongoose.Schema({
  den1: Number,
  den2: Number,
  den3: {
    status: Number,
    time: String, // Lưu giờ phút giây dưới dạng string "HH:mm:ss"
  },
});
const Light = mongoose.model("Light", LightSchema);

// Schema cho cảm biến và đèn 4
const SensorSchema = new mongoose.Schema({
  den4: Number,
  temperature: Number,
  humidity: Number,
});
const Sensor = mongoose.model("Sensor", SensorSchema);

// Dữ liệu mẫu ban đầu
const seedData = async () => {
  const lightExists = await Light.findOne();
  if (!lightExists) {
    await Light.create({ den1: 0, den2: 0, den3: { status: 0, time: "00:00:00" } });
  }

  const sensorExists = await Sensor.findOne();
  if (!sensorExists) {
    await Sensor.create({ den4: 0, temperature: 25.0, humidity: 60.0 });
  }
};
seedData();

// 1. Lấy trạng thái đèn 1, 2, 3
app.get("/api/lights/status", async (req, res) => {
  const light = await Light.findOne();
  res.json({ success: true, lights: light });
});

// 2. Cập nhật trạng thái đèn 1, 2, 3 từ FE
app.post("/api/lights/update", async (req, res) => {
    console.log("Nhận yêu cầu POST cập nhật đèn:", req.body);
    const { lights } = req.body;
    await Light.findOneAndUpdate({}, lights, { upsert: true });
    console.log("Trạng thái đèn sau cập nhật:", lights);
    res.json({ success: true, message: "Lights status updated successfully." });
  });
  
  // Kiểm tra và cập nhật trạng thái đèn 3 liên tục
  setInterval(async () => {
    const light = await Light.findOne();
    if (light && light.den3 && light.den3.time) {
      const [hours, minutes] = light.den3.time.split(":").map(Number);
      const now = new Date();
  
      console.log("Kiểm tra thời gian hiện tại:", now);
      console.log("Thời gian hẹn giờ:", light.den3.time);
  
      if (now.getHours() === hours && now.getMinutes() === minutes) {
        console.log("Cập nhật trạng thái đèn 3 về 0.");
        await Light.findOneAndUpdate({}, { "den3.status": 0, "den3.time": "00:00:00" });
      }
    }
  }, 3000); // Kiểm tra mỗi nửa phút

// 3. Cập nhật trạng thái đèn 4, nhiệt độ và độ ẩm từ Kit
app.post("/api/sensor/update", async (req, res) => {
  const { den4, temperature, humidity } = req.body;
  await Sensor.findOneAndUpdate({}, { den4, temperature, humidity }, { upsert: true });
  res.json({ success: true, message: "Sensor data updated successfully." });
});


// 4. Lấy trạng thái đèn 4, nhiệt độ và độ ẩm
app.get("/api/sensor/status", async (req, res) => {
  const sensor = await Sensor.findOne();
  res.json({
    success: true,
    den4: sensor.den4,
    temperature: sensor.temperature,
    humidity: sensor.humidity,
  });
});

// 5. Bật/tắt đèn 4 từ FE
app.post("/api/sensor/toggle", async (req, res) => {
  const { den4 } = req.body;
  await Sensor.findOneAndUpdate({}, { den4 }, { upsert: true });
  res.json({ success: true, message: `Light 4 is ${den4 ? 'ON' : 'OFF'}` });
});

// 6. Lấy trạng thái đèn 4
app.get("/api/sensor/light4/status", async (req, res) => {
  const sensor = await Sensor.findOne();
  res.json({ success: true, den4: sensor.den4 });
});

// Chạy server
const PORT = 8000;
app.listen(PORT, () => {
  console.log(`Server chạy tại http://localhost:${PORT}`);
});

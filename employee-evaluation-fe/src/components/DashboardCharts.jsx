import React, { useEffect, useState } from "react";
import axios from "axios";
import "../css/chart.css";

import {
  Bar,
  Pie,
  Line,
} from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title,
} from "chart.js";

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title
);

const DashboardCharts = () => {
  const [cycles, setCycles] = useState([]);
  const [selectedCycle, setSelectedCycle] = useState(null);
  const [progress, setProgress] = useState(null);
  const [distribution, setDistribution] = useState([]);
  const [topEmployees, setTopEmployees] = useState([]);
  const [bottomEmployees, setBottomEmployees] = useState([]);
  const [averageScores, setAverageScores] = useState([]);

  const api = axios.create({
    baseURL: "http://localhost:8080/api/dashboard",
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

  useEffect(() => {
    const fetchCycles = async () => {
      try {
        const res = await api.get("/average-scores");
        if (res.data?.data) {
          setCycles(res.data.data);
          setSelectedCycle(res.data.data[0]?.evaluationCycleId || null);
          setAverageScores(res.data.data);
        }
      } catch (err) {
        console.error("❌ Error fetching cycles", err);
      }
    };
    fetchCycles();
  }, []);

  useEffect(() => {
    if (!selectedCycle) return;

    const fetchData = async () => {
      try {
        const [progressRes, distributionRes, topRes, bottomRes] = await Promise.all([
          api.get(`/progress/${selectedCycle}`),
          api.get(`/distribution/${selectedCycle}`),
          api.get(`/top/${selectedCycle}?limit=5`),
          api.get(`/bottom/${selectedCycle}?limit=5`),
        ]);

        setProgress(progressRes.data.data);
        setDistribution(distributionRes.data.data);
        setTopEmployees(topRes.data.data);
        setBottomEmployees(bottomRes.data.data);
      } catch (err) {
        console.error("❌ Error fetching charts data", err);
      }
    };

    fetchData();
  }, [selectedCycle]);

  const progressData = progress && {
    labels: ["Đã đánh giá", "Chưa đánh giá"],
    datasets: [
      {
        data: [progress.evaluatedCount, progress.notEvaluatedCount],
        backgroundColor: ["#36A2EB", "#FF6384"],
      },
    ],
  };

  const distributionData = {
    labels: distribution.map((d) => d.range),
    datasets: [
      {
        label: "Số nhân viên",
        data: distribution.map((d) => d.count),
        backgroundColor: "#42A5F5",
      },
    ],
  };

  const topData = {
    labels: topEmployees.map((e) => e.employeeName),
    datasets: [
      {
        label: "Điểm trung bình",
        data: topEmployees.map((e) => e.averageScore),
        backgroundColor: "#66BB6A",
      },
    ],
  };

  const bottomData = {
    labels: bottomEmployees.map((e) => e.employeeName),
    datasets: [
      {
        label: "Điểm trung bình",
        data: bottomEmployees.map((e) => e.averageScore),
        backgroundColor: "#EF5350",
      },
    ],
  };

  const averageScoreData = {
    labels: averageScores.map((c) => c.evaluationCycleName),
    datasets: [
      {
        label: "Điểm trung bình",
        data: averageScores.map((c) => c.averageScore),
        borderColor: "#FFA726",
        fill: false,
        tension: 0.3,
      },
    ],
  };

  return (
    <div className="chart-container">
      <h1 className="chart-text">📊 Báo cáo tổng quát</h1>

      {/* Bộ lọc chu kỳ */}
      <div className="chart-filter">
        <label className="chart-option">Chọn chu kỳ đánh giá:</label>
        <select
          value={selectedCycle || ""}
          onChange={(e) => setSelectedCycle(e.target.value)}
          className="chart-select"
        >
          {cycles.map((cycle) => (
            <option key={cycle.evaluationCycleId} value={cycle.evaluationCycleId}>
              {cycle.evaluationCycleName}
            </option>
          ))}
        </select>
      </div>

      {/* Lưới chia 2 cột */}
      <div className="chart-grid">
         {/* Chart 1 */}
        <div className="chart-box-1">
            <h2 className="text-lg font-semibold mb-2 self-start">Tiến độ đánh giá</h2>
            <div className="w-[250px] h-[250px] flex items-center justify-center">
            {progressData ? (
                <Pie
                data={progressData}
                options={{
                    maintainAspectRatio: false,
                    plugins: { legend: { position: "bottom" } },
                }}
                />
            ) : (
                <p>Đang tải...</p>
            )}
            </div>
        </div>

        {/* Chart 2 */}
        <div className="chart-box-2">
            <h2 className="text-lg font-semibold mb-2">Phân bố điểm</h2>
            <div className="h-[220px]">
            <Bar
                data={distributionData}
                options={{
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true, ticks: { stepSize: 1 } },
                },
                plugins: { legend: { display: false } },
                }}
            />
            </div>
        </div>

        {/* Chart 3 */}
        <div className="chart-box-3">
          <h2 className="text-xl font-bold mb-3">Top nhân viên</h2>
          <div className="h-[300px]">
            <Bar data={topData} options={{ maintainAspectRatio: false }} />
          </div>
        </div>

        {/* Chart 4 */}
        <div className="chart-box-4">
          <h2 className="text-xl font-bold mb-3">Nhân viên điểm thấp</h2>
          <div className="h-[300px]">
            <Bar data={bottomData} options={{ maintainAspectRatio: false }} />
          </div>
        </div>

        {/* Chart 5 */}
        <div className="chart-box-5">
          <h2 className="text-xl font-bold mb-3">Điểm trung bình qua các chu kỳ</h2>
          <div className="h-[350px]">
            <Line data={averageScoreData} options={{ maintainAspectRatio: false }} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardCharts;

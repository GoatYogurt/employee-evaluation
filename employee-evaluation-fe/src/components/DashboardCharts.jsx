import React, { useEffect, useState } from "react";
import axios from "axios";
import "../css/chart.css";

import { Bar, Pie, Line } from "react-chartjs-2";
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
  const [topLimit, setTopLimit] = useState(5);
  const [bottomLimit, setBottomLimit] = useState(5);

  const api = axios.create({
    baseURL: "http://localhost:8080/api/dashboard",
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

  // Lấy danh sách chu kỳ
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

  // Lấy dữ liệu chính khi chọn chu kỳ hoặc thay đổi limit
  useEffect(() => {
    if (!selectedCycle) return;

    const fetchData = async () => {
      try {
        const [progressRes, distributionRes, topRes, bottomRes] = await Promise.all([
          api.get(`/progress/${selectedCycle}`),
          api.get(`/distribution/${selectedCycle}`),
          api.get(`/top/${selectedCycle}?limit=${topLimit}`),
          api.get(`/bottom/${selectedCycle}?limit=${bottomLimit}`),
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
  }, [selectedCycle, topLimit, bottomLimit]);

  // Biểu đồ tiến độ
  const progressData =
    progress && {
      labels: ["Đã đánh giá", "Chưa đánh giá"],
      datasets: [
        {
          data: [progress.evaluatedCount, progress.notEvaluatedCount],
          backgroundColor: ["#36A2EB", "#FF6384"],
        },
      ],
    };

  // Biểu đồ phân bố điểm
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

  // Biểu đồ đường điểm trung bình
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
      <h1 className="chart-text">Báo cáo tổng quát</h1>


      <div className="eva-chart-form">
        <div className="eva-chart">
          <h2 className="text-xl font-bold mb-3">Điểm trung bình qua các chu kỳ</h2>
          <div className="h-[350px]">
            <Line data={averageScoreData} options={{ maintainAspectRatio: false }} />
          </div>
        </div>
      </div>

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
      </div>

    <div className="top-bottom">
        <div className="top-list">
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-bold">Nhân viên điểm cao</h2>
            <select
              value={topLimit}
              onChange={(e) => setTopLimit(Number(e.target.value))}
              className="border rounded-md p-1 text-sm"
            >
              {[3, 5, 10, 15].map((n) => (
                <option key={n} value={n}>
                  Hiển thị {n}
                </option>
              ))}
            </select>
          </div>
          <table className="w-full text-sm border border-gray-200 rounded-lg">
            <thead className="bg-gray-100">
              <tr>
                <th className="p-2 text-left">#</th>
                <th className="p-2 text-left">Tên nhân viên</th>
                <th className="p-2 text-right">Điểm trung bình</th>
              </tr>
            </thead>
            <tbody>
              {topEmployees.length > 0 ? (
                topEmployees.map((emp, i) => (
                  <tr key={i} className="border-t hover:bg-gray-50">
                    <td className="p-2">{i + 1}</td>
                    <td className="p-2">{emp.employeeName}</td>
                    <td className="p-2 text-right">{emp.averageScore.toFixed(2)}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3" className="p-3 text-center text-gray-500">
                    Không có dữ liệu
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="bottom-list">
          <div className="flex justify-between items-center mb-3">
            <h2 className="text-xl font-bold">Nhân viên điểm thấp</h2>
            <select
              value={bottomLimit}
              onChange={(e) => setBottomLimit(Number(e.target.value))}
              className="border rounded-md p-1 text-sm"
            >
              {[3, 5, 10, 15].map((n) => (
                <option key={n} value={n}>
                  Hiển thị {n}
                </option>
              ))}
            </select>
          </div>
          <table className="w-full text-sm border border-gray-200 rounded-lg">
            <thead className="bg-gray-100">
              <tr>
                <th className="p-2 text-left">#</th>
                <th className="p-2 text-left">Tên nhân viên</th>
                <th className="p-2 text-right">Điểm trung bình</th>
              </tr>
            </thead>
            <tbody>
              {bottomEmployees.length > 0 ? (
                bottomEmployees.map((emp, i) => (
                  <tr key={i} className="border-t hover:bg-gray-50">
                    <td className="p-2">{i + 1}</td>
                    <td className="p-2">{emp.employeeName}</td>
                    <td className="p-2 text-right">{emp.averageScore.toFixed(2)}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3" className="p-3 text-center text-gray-500">
                    Không có dữ liệu
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
    </div>
    </div>
  );
};

export default DashboardCharts;

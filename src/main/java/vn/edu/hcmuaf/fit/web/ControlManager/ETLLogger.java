package vn.edu.hcmuaf.fit.web.ControlManager;

import java.sql.*;
import java.time.LocalDateTime;

public class ETLLogger {
    private final Connection conn;

    public ETLLogger(Connection conn) {
        this.conn = conn;
    }

    public int startLog(String stepName) {
        String sql = "INSERT INTO etl_log (step_name, start_time, status, log_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, stepName);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, "RUNNING");
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về id log
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void endLog(int logId, String status) {
        String sql = "UPDATE etl_log SET end_time = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, status);
            ps.setInt(3, logId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

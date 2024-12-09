package de.honoka.qqrobot.starter.component.logger.dao;

import de.honoka.qqrobot.starter.component.logger.LoggerServer;
import de.honoka.qqrobot.starter.component.logger.entity.UsageLog;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsageLogDao {

    @Resource
    private LoggerServer loggerServer;

    @SneakyThrows
    public int getCount() {
        String sql = "select count(*) from `usage_log`";
        try(Connection connection = loggerServer.getConnection();
            Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(sql)) {
                if(resultSet.next()) {
                    return resultSet.getInt(1);
                }
                throw new SQLException("SQL Error：" + sql);
            }
        }
    }

    @SneakyThrows
    public List<UsageLog> selectPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "select * from `usage_log` order by `datetime` desc limit ? offset ?";
        try(Connection connection = loggerServer.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageSize);
            statement.setInt(2, offset);
            List<UsageLog> list = new ArrayList<>();
            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    list.add(parseLog(resultSet));
                }
            }
            return list;
        }
    }

    @SneakyThrows
    public UsageLog parseLog(ResultSet resultSet) {
        return new UsageLog()
            .setId(resultSet.getInt("id"))
            .setQq(resultSet.getLong("qq"))
            .setDatetime(resultSet.getTimestamp("datetime"))
            .setGroupName(resultSet.getString("groupName"))
            .setUsername(resultSet.getString("username"))
            .setMsg(resultSet.getString("msg"))
            .setReply(resultSet.getString("reply"));
    }

    @SneakyThrows
    public void insert(UsageLog log) {
        String sql = "insert into `usage_log` (`datetime`, `qq`, `groupName`, `username`, " +
            "`msg`, `reply`) values (?, ?, ?, ?, ?, ?)";
        try(Connection connection = loggerServer.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, new Timestamp(log.getDatetime().getTime()));
            statement.setLong(2, log.getQq());
            statement.setString(3, log.getGroupName());
            statement.setString(4, log.getUsername());
            statement.setString(5, log.getMsg());
            statement.setString(6, log.getReply());
            statement.execute();
        }
    }
}

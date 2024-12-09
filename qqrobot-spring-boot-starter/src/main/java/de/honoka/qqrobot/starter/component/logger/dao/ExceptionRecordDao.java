package de.honoka.qqrobot.starter.component.logger.dao;

import de.honoka.qqrobot.starter.component.logger.LoggerServer;
import de.honoka.qqrobot.starter.component.logger.entity.ExceptionRecord;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionRecordDao {

    @Resource
    private LoggerServer loggerServer;

    @SneakyThrows
    public List<ExceptionRecord> readException(int maxSize) {
        String sql = "select * from `exception_record` order by `datetime` desc limit ?";
        try(Connection connection = loggerServer.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maxSize);
            List<ExceptionRecord> list = new ArrayList<>();
            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    list.add(parseRecord(resultSet));
                }
            }
            return list;
        }
    }

    @SneakyThrows
    private ExceptionRecord parseRecord(ResultSet resultSet) {
        return new ExceptionRecord()
            .setId(resultSet.getInt("id"))
            .setDatetime(resultSet.getTimestamp("datetime"))
            .setExceptionText(resultSet.getString("exceptionText"));
    }

    @SneakyThrows
    public void insert(ExceptionRecord record) {
        String sql = "insert into `exception_record` (`datetime`, `exceptionText`) values (?, ?)";
        try(Connection connection = loggerServer.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, new Timestamp(record.getDatetime().getTime()));
            statement.setString(2, record.getExceptionText());
            statement.execute();
        }
    }
}

package com.noeun.youcaloid.db;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataBaseService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String url ="jdbc:mariadb://localhost:3306/youcaloid";
    private String name = "myadmin";
    private String password = "root";

	// @Value("${spring.datasource.url}")
	// public void setUrlValue(String value){
	// 	url = "jdbc:mariadb://localhost:3306/youcaloid";
	// }

    // @Value("${spring.datasource.username}")
    // public void setNameValue(String value){
    //     name = "myadmin";
    // }

    // @Value("${spring.datasource.password}")
    // public void setpwValue(String value){
    //     password = "root";
    // }

    public DataBaseService(){
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl(url);
            dataSource.setUser(name);
            dataSource.setPassword(password);

        } catch (SQLException e) {
            System.out.println("db connect fail.");
            e.printStackTrace();
        }
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getModelId(String guildId, String userId){
        return jdbcTemplate.queryForList(
            String.format("SELECT MODELID FROM user_info WHERE GUILDID = %s AND USERID = %s",guildId,userId)
            , String.class).get(0);
    }

    public int addModelId(String guildId, String userId, String modelId){
        if (jdbcTemplate.queryForList(
            String.format("SELECT MODELID FROM user_info WHERE GUILDID = %s AND USERID = %s",guildId,userId)
            , String.class).size() == 0){
        return jdbcTemplate.update("INSERT INTO user_info (GUILDID, USERID, MODELID) VALUES (?, ?, ?)",guildId,userId,modelId);
            }else{
                return jdbcTemplate.update("UPDATE user_info SET MODELID = ? WHERE GUILDID = ? AND USERID = ? ",modelId,guildId,userId);
            }
    }

    public String nowModel(String guildId, String userId){
        return jdbcTemplate.queryForList(
            String.format("SELECT model_info.DESCRIPTION FROM user_info INNER JOIN model_info ON user_info.MODELID = model_info.MODELID WHERE GUILDID = %s AND USERID = %s",guildId,userId)
            , String.class).get(0);
    }
}

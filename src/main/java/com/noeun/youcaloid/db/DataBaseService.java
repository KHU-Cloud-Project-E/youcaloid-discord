package com.noeun.youcaloid.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DataBaseService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static String url;
    private static String name;
    private static String password;

	@Value("${db.url}")
	public void setUrlValue(String value){
		url = value;
	}

    @Value("${db.username}")
	public void setNameValue(String value){
		name = value;
	}

    @Value("${db.password}")
	public void setPasswordValue(String value){
		password = value;
	}

    public static String getUrl() {
        return url;
    }

    public static String getName(){
        return name;
    }

    public static String getPassword() {
        return password;
    }

    public DataBaseService(){
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl(getUrl());
            dataSource.setUser(getName());
            dataSource.setPassword(getPassword());
            System.out.println("db 연결에 성공한것 같읍니다?");

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
            String.format("SELECT model_info.NAME FROM user_info INNER JOIN model_info ON user_info.MODELID = model_info.MODELID WHERE GUILDID = %s AND USERID = %s",guildId,userId)
            , String.class).get(0);
    }

    public String getModelDec(String modelId){
         return jdbcTemplate.queryForList(
            String.format("SELECT NAME FROM model_info WHERE MODELID = %s", modelId)
            , String.class).get(0);
    }

    public int setMacro(String userId, int macroNum, String modelId){
        if(jdbcTemplate.queryForList(
            String.format(
                "SELECT MODELID FROM model_info WHERE MODELID = %s", 
                modelId), String.class).size() > 0
        ){
            if(jdbcTemplate.queryForList(String.format("SELECT MODELID FROM macro WHERE USERID = %s AND MACRONUM = %s", 
                userId, String.valueOf(macroNum)), String.class).size() > 0){
                    return jdbcTemplate.update("UPDATE  macro SET MODELID = ? WHERE USERID = ? AND MACRONUM = ?", modelId, userId, String.valueOf(macroNum));
                }else{
                    return jdbcTemplate.update("INSERT INTO macro (USERID, MACRONUM, MODELID) VALUES (?, ?, ?)",userId, String.valueOf(macroNum), modelId);
                }
        }

        return 0;
    }

    public int changeModel(String guildId, String userId, int macroNum){
        if(jdbcTemplate.queryForList(String.format(
            "SELECT MODELID FROM macro WHERE USERID = %s AND MACRONUM = %s", 
            userId, String.valueOf(macroNum)), String.class).size() > 0){
                String modelId = jdbcTemplate.queryForList(String.format(
            "SELECT MODELID FROM macro WHERE USERID = %s AND MACRONUM = %s", 
            userId, String.valueOf(macroNum)), String.class).get(0);
            return addModelId(guildId, userId, modelId);
        }
        return 0;
    }

    public String getMacro(String userId){
        String rString = "";
        List<MacroType> macros = jdbcTemplate.query(String.format("SELECT macro.MACRONUM, model_info.NAME FROM macro INNER JOIN model_info ON macro.MODELID = model_info.MODELID WHERE macro.USERID = %s", userId),
        new RowMapper<MacroType>(){
            @Override
            public MacroType mapRow(ResultSet rs, int rowNum) throws SQLException {
                MacroType macroType = new MacroType();
                macroType.setMacroNum(rs.getString("macro.MACRONUM"));
                macroType.setModelDec(rs.getString("model_info.NAME"));
                return macroType;
            }
        });
        if(macros.isEmpty()){
            rString = "macro not found";
        }else{
            for(MacroType m : macros){
                rString = rString + String.format("%s : %s\n", m.getMacroNum(), m.getModelDec());
            }
        }
        return rString;
    }


}

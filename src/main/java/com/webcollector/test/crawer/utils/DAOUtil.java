package com.webcollector.test.crawer.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 数据库操作通用类
 * @author Hu Wei
 * @since 2016年4月17日
 */
public class DAOUtil<T> {
	
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 默认构造函数
	 */
	public DAOUtil(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 适用于带实体bean参数的数据更新操作：insert,update,delete
	 * @author Hu Wei
	 * @date 2016年4月17日
	 * @param namedSql
	 * @param javaBean
	 * @return
	 */
	public Map<String,Object> update(String namedSql, T javaBean) {
		if(jdbcTemplate == null){
			return null;
		}
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int count = namedParameterJdbcTemplate.update(namedSql, paramSource, keyHolder);
		if(count == 0){
			return new HashMap<>();
		}
		return keyHolder.getKeys(); 
	}
	
	/**
	 * 适用于List参数的数据批量更新操作
	 * @author jianglu
	 * @date 2016年7月13日
	 * @param namedSql
	 * @param list
	 * @return
	 */
	public int batchUpdate(String namedSql, List<T> list){
		if(jdbcTemplate == null){
			return 0;
		}
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		SqlParameterSource[] sqlParameterSources = new BeanPropertySqlParameterSource[list.size()];
		for(int i=0;i<list.size();i++){
			sqlParameterSources[i] = new BeanPropertySqlParameterSource(list.get(i));
		}
		int[] num = namedParameterJdbcTemplate.batchUpdate(namedSql,sqlParameterSources);
		return num.length;
	}
}

/*String sql = "insert into tequipmentmaintain(projectid,towerid,equipmenttype,faulttype,faultdate,repairsdate,maintaindate,"
		+ "maintainstate,maintainengineer,maintainengineertel,repairsperson,repairstel,repairsemail,earrantystate,"
		+ "customer,customertel,faultdescription,remark,stateid) " 
		+ "values(:projectID,:towerID,:equipmentType,:faultType,:faultDate,:repairsDate,:maintainDate,:maintainState,"
		+ ":maintainEngineer,:maintainEngineerTel,:repairsPerson,:repairsTel,:repairsEmail,:earrantyState,"
		+ ":customer,:customerTel,:faultDescription,:remark,1)";

DAOUtil<EquipmentMaintain> daoUtil = new DAOUtil<>(jdbcTemplate);
Map<String,Object> map = daoUtil.update(sql, equipmentMaintain);
int maintainID = (Integer) map.get("maintainID");*/

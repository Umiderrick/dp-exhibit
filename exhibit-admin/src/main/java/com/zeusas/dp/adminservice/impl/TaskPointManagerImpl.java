/**
 * 
 */
package com.zeusas.dp.adminservice.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zeusas.dp.adminservice.TaskPointManager;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.UserCustomer;
import com.zeusas.dp.exhibit.entity.UserDetail;
import com.zeusas.dp.exhibit.service.AuthUserService;
import com.zeusas.dp.exhibit.service.UserCustomerService;
import com.zeusas.dp.exhibit.service.UserDetailService;
import com.zeusas.dp.exhibit.utils.ServiceException;
import com.zeusas.dp.util.DBMS;
import com.zeusas.dp.util.DataDDL;
import com.zeusas.dp.util.Database;
import com.zeusas.dp.util.Meta;
import com.zeusas.dp.util.Record;
import com.zeusas.dp.util.Table;

/**
 * @author pengbo
 *
 */
@Service
public class TaskPointManagerImpl implements TaskPointManager {

	final static Logger logger = LoggerFactory.getLogger(TaskPointManagerImpl.class);
	private static final String AUTHUSER = "AUTHUSER";
	private static final String USERDETAIL = "USERDETAIL";
	private static final String USERCUSTOMER = "USERCUSTOMER";

	static {
		DBMS.load("ddl/ordm_syncdata.xml");
	}

	private DataDDL ddl = DBMS.getItem("ORDM");

	@Autowired
	private AuthUserService aus;

	@Autowired
	private UserCustomerService userCustomerS;

	@Autowired
	private UserDetailService userDetailService;

	@Override
	public <T> int diffupdate(List<T> list) {
		if (list == null || list.size() == 0) {
			return 0;
		}
		T t = list.get(0);
		try {
			if (t instanceof AuthUser) {
				for (T x : list) {
					AuthUser au = (AuthUser) x;
					if (aus.get(au.getUid()) != null) {
						aus.update(au);
					} else {
						aus.save(au);
					}
				}
			} else if (t instanceof UserCustomer) {
				for (T x : list) {
					UserCustomer ucus = (UserCustomer) x;
					if (userCustomerS.get(ucus.getUserId()) != null) {
						userCustomerS.update(ucus);
					} else {
						userCustomerS.save(ucus);
					}
				}
			} else if (t instanceof UserDetail) {
				for (T x : list) {
					UserDetail ud = (UserDetail) x;
					if (userDetailService.get(ud.getUserId()) != null) {
						userDetailService.update(ud);
					} else {
						userDetailService.save(ud);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return list.size();
	}

	@Override
	public void doReload(int i) {
		Database prepareDB = new Database(ddl);
		int diffUpdate;
		List<Record> records;
		Meta meta;
		try {
			Connection connect = prepareDB.connect();
			switch (i) {
			case 1:
				meta= ddl.getMeta(AUTHUSER);
				records = getList(connect, meta);
				List<AuthUser> nlau = new ArrayList<>(records.size());
				for (Record r : records) {
					String jsonString = prep(r.toJSONObject(meta).toJSONString());
					nlau.add(JSON.parseObject(jsonString, AuthUser.class));
				}
				diffUpdate =diffupdate(nlau);
				logger.info(diffUpdate + "条authuser已更新");
				break;
			case 2:
				meta= ddl.getMeta(USERDETAIL);
				records = getList(connect, meta);
				List<UserDetail> userDetails = new ArrayList<>(records.size());
				for (Record r : records) {
					String jsonString = prep(r.toJSONObject(meta).toJSONString());
					userDetails.add(JSON.parseObject(jsonString, UserDetail.class));
				}
				diffUpdate = diffupdate(userDetails);
				logger.info(diffUpdate + "条userdetail已更新");
				break;
			case 3:
				meta= ddl.getMeta(USERCUSTOMER);
				records = getList(connect, meta);
				List<UserCustomer> userCustomers = new ArrayList<>(records.size());
				for (Record r : records) {
					String jsonString = prep(r.toJSONObject(meta).toJSONString());
					if (jsonString.indexOf("null") > 0) {
						continue;
					}
					userCustomers.add(JSON.parseObject(jsonString, UserCustomer.class));
				}
				diffUpdate = diffupdate(userCustomers);
				logger.info(diffUpdate + "条usercustomer已更新");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			prepareDB.close();
		}
	}


	private List<Record> getList(Connection conn, Meta meta, Object... args) {
		Table tb;
		try {
			tb = new Table(meta, conn);
			tb.open(meta.getStmt(), args);
		} catch (SQLException e) {
			logger.error("异常SQL-{}", meta.getStmt());
			throw new ServiceException(e);
		}
		return tb.values();
	}

	/**
	 * @param jsonString
	 * @return
	 */
	private String prep(String jsonString) {
		return jsonString.replace("\"[", "[").replace("]\"", "]").replace("\\", "");
	}

}

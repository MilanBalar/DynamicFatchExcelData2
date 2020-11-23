package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletRequest;

import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.journaldev.spring.model.MobileInfo;

public class DAO {

	HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}

	public static int insertData(ServletRequest request) throws ClassNotFoundException, SQLException {
		int column = (Integer) request.getAttribute("totalColumn");
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection con = DriverManager.getConnection("jdbc:sqlserver://192.168.100.55:1440;databaseName=eTest", "milan",
				"F~?'7{e{");
		PreparedStatement ps = con.prepareStatement("insert into MobileInfo values(" + getColumn(null) + ")");

		for (int i = 1; i <= column; i++) {

			ps.setString(i, (String) request.getAttribute("cell"));
		}

		int m = ps.executeUpdate();
		return m;
	}

	private static String getColumn(ServletRequest request) {
		String a = null;
		int column = (Integer) request.getAttribute("totalColumn");
		for (int i = 0; i < column - 1; i++) {

			a += "?,";
		}
		a += "?";
		return a;
	}

	@Transactional
	public void save(List<MobileInfo> mobileList) {

		for (MobileInfo mobile : mobileList) {

			ht.save(mobile);
		}
	}

}

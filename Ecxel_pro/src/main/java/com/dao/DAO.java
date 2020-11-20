package com.dao;

import java.util.List;

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

	@Transactional
	public void save(List<MobileInfo> mobileList) {

		for (MobileInfo mobile : mobileList) {

			ht.save(mobile);
		}
	}

}

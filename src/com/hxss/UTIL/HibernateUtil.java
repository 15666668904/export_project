package com.hxss.UTIL;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	private static Configuration configuration = new Configuration();
	// 创建线程局部变量threadLocal,用来保存hibernate的Session
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	// 使用静态块初始化hibernate
	static {
		try {
			Configuration cfg = new Configuration().configure();// 读取配置文件hibernate.cfg.xml
			sessionFactory = cfg.buildSessionFactory();// 创建SessionFactory
		} catch (Throwable ex) {
			// TODO: handle exception
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}

	// 获取SessionFactory实例
	public static SessionFactory getSessionFactory() {
		return sessionFactory;

	}

	// 获取ThreadLocal对象关系的Session实例
	public static Session getSession() throws HibernateException {
		Session session = (Session) threadLocal.get();
		if (session == null || !session.isOpen()) {
			if (sessionFactory == null) {
				rebuildSessionFactory();
			}
			// 通过SessionFactory对象创建Session
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			// 将打开的Session实例保存到线程局部变量threadLocal中
			threadLocal.set(session);
		}
		return session;
	}

	// 重建SessionFactory
	public static void rebuildSessionFactory() {
		try {
			configuration.configure("/hibernate.cfg.xml");// 读取配置文件hibernate.cfg.xml
			sessionFactory = configuration.buildSessionFactory();// 创建SessionFactory
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SessionFactory创建失败");
			e.printStackTrace();
		}
	}

	// 关闭Session实例
	public static void closeSession() {
		// 从线程局部变量threadLocal中获取之前存入的Session实例
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		if (session != null) {
			session.close();
		}
	}

	// 关闭缓存和连接池
	public static void shutdown() {
		getSessionFactory().close();
	}
}

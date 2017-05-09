package com.spring.project2.dao;

import java.io.IOException;
import java.sql.Blob;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.project2.model.Document;

@Repository
public class DocumentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public void save(Document document) {
		Session session = sessionFactory.getCurrentSession();
		session.save(document);
	}

	@Transactional
	public List<Document> list() {
		Session session = sessionFactory.getCurrentSession();
		List<Document> documents = null;
		try {
			documents = (List<Document>) session.createQuery("from Document").list();

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return documents;
	}

	@Transactional
	public Document get(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Document) session.get(Document.class, id);
	}

	@Transactional
	public void remove(Integer id) {
		Session session = sessionFactory.getCurrentSession();

		Document document = (Document) session.get(Document.class, id);

		session.delete(document);
	}

	public void saveDocu(Document document, MultipartFile file) throws IOException {
		Session session = sessionFactory.getCurrentSession();
		byte[] bytes = IOUtils.toByteArray(file.getInputStream());
		Blob blob = Hibernate.getLobCreator(session).createBlob(bytes);
		document.setFilename(file.getOriginalFilename());
		document.setContent(blob);
		document.setContentType(file.getContentType());
	}

}

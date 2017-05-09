package com.spring.project2.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.project2.dao.DocumentDAO;
import com.spring.project2.model.Document;

@Controller
public class DocumentController {

	@Autowired
	private DocumentDAO documentDao;

	@RequestMapping("/index")
	public String index(Map<String, Object> map) {
		try {
			map.put("document", new Document());
			map.put("documentList", documentDao.list());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "documents";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("document") Document document, @RequestParam("file") MultipartFile file) {

		System.out.println("Name:" + document.getName());
		System.out.println("Desc:" + document.getDescription());
		System.out.println("File:" + file.getName());
		System.out.println("ContentType:" + file.getContentType());

		try {
			documentDao.saveDocu(document, file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			documentDao.save(document);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/index.html";
	}

	@RequestMapping("/download/{documentId}")
	public String download(@PathVariable("documentId") Integer documentId, HttpServletResponse response) {

		Document doc = documentDao.get(documentId);
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + doc.getFilename() + "\"");
			OutputStream out = response.getOutputStream();
			response.setContentType(doc.getContentType());
			IOUtils.copy(doc.getContent().getBinaryStream(), out);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping("/remove/{documentId}")
	public String remove(@PathVariable("documentId") Integer documentId) {

		documentDao.remove(documentId);

		return "redirect:/index.html";
	}

}

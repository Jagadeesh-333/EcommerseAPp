package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import com.jtspringproject.JtSpringProject.controller.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.categoryDao;
import com.jtspringproject.JtSpringProject.models.Category;

import net.bytebuddy.dynamic.DynamicType.Builder.InnerTypeDefinition;

@Service
public class categoryService {
	@Autowired
	private categoryDao categoryDao;
	
	public Category addCategory(String name) {
		return this.categoryDao.addCategory(name);
	}
	
	public List<Category> getCategories(){
		return this.categoryDao.getCategories();
	}
	
	public Boolean deleteCategory(int id) {
		return this.categoryDao.deletCategory(id);
	}
	
	public Category updateCategory(int id,String name) {
		return this.categoryDao.updateCategory(id, name);
	}

//	public Category getCategory(int id) {
//		return this.categoryDao.getCategory(id);
//	}
	// Updated method to handle missing categories
	public Category getCategory(int id) {
		Category category = this.categoryDao.getCategory(id);
		if (category == null) {
			throw new CategoryNotFoundException("Category not found for id: " + id);
		}
		return category;
	}
}

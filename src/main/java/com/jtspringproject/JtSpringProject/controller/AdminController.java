package com.jtspringproject.JtSpringProject.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final userService userService;
	private final categoryService categoryService;
	private final productService productService;
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	public AdminController(userService userService, categoryService categoryService, productService productService) {
		this.userService = userService;
		this.categoryService = categoryService;
		this.productService = productService;
	}

	@GetMapping("/index")
	public String index(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("username", username);
		return "index";
	}

	@GetMapping("login")
	public ModelAndView adminLogin(@RequestParam(required = false) String error) {
		ModelAndView mv = new ModelAndView("adminlogin");
		if ("true".equals(error)) {
			mv.addObject("msg", "Invalid username or password. Please try again.");
		}
		return mv;
	}

	@GetMapping(value = {"/", "Dashboard"})
	public ModelAndView adminHome(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ModelAndView mv = new ModelAndView("adminHome");
		mv.addObject("admin", authentication.getName());
		return mv;
	}

	@GetMapping("categories")
	public ModelAndView getCategory() {
		ModelAndView mView = new ModelAndView("categories");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		return mView;
	}

	@PostMapping("/categories")
	public String addCategory(@RequestParam("categoryname") String categoryName) {
		logger.info("Adding category: {}", categoryName);

		Category category = this.categoryService.addCategory(categoryName);
		return "redirect:/admin/categories";
	}

	@GetMapping("categories/delete")
	public String removeCategoryDb(@RequestParam("id") int id) {
		this.categoryService.deleteCategory(id);
		return "redirect:/admin/categories";
	}

	@GetMapping("categories/update")
	public String updateCategory(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryName) {
		this.categoryService.updateCategory(id, categoryName);
		return "redirect:/admin/categories";
	}

	@GetMapping("products")
	public ModelAndView getProduct() {
		ModelAndView mView = new ModelAndView("products");
		List<Product> products = this.productService.getProducts();

		if (products.isEmpty()) {
			mView.addObject("msg", "No products are available");
		} else {
			mView.addObject("products", products);
		}
		return mView;
	}

	@GetMapping("products/add")
	public ModelAndView addProduct() {
		ModelAndView mView = new ModelAndView("productsAdd");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		return mView;
	}

	@PostMapping("products/add")
	public String addProduct(@RequestParam("name") String name,
							 @RequestParam("categoryid") int categoryId,
							 @RequestParam("price") int price,
							 @RequestParam("weight") int weight,
							 @RequestParam("quantity") int quantity,
							 @RequestParam("description") String description,
							 @RequestParam("productImage") String productImage) {
		logger.info("Adding product: name={}, categoryId={}, price={}", name, categoryId, price);

		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();
		product.setCategory(category);
		product.setName(name);
		product.setDescription(description);
		product.setPrice(price);
		product.setImage(productImage);
		product.setWeight(weight);
		product.setQuantity(quantity);
		this.productService.addProduct(product);

		return "redirect:/admin/products";
	}

	@GetMapping("products/update/{id}")
	public ModelAndView updateProduct(@PathVariable("id") int id) {
		ModelAndView mView = new ModelAndView("productsUpdate");
		Product product = this.productService.getProduct(id);
		List<Category> categories = this.categoryService.getCategories();

		mView.addObject("categories", categories);
		mView.addObject("product", product);
		return mView;
	}

	@PostMapping("products/update/{id}")
	public String updateProduct(@PathVariable("id") int id,
								@RequestParam("name") String name,
								@RequestParam("categoryid") int categoryId,
								@RequestParam("price") int price,
								@RequestParam("weight") int weight,
								@RequestParam("quantity") int quantity,
								@RequestParam("description") String description,
								@RequestParam("productImage") String productImage) {
		// Add your update logic here
		return "redirect:/admin/products";
	}

	@GetMapping("products/delete")
	public String removeProduct(@RequestParam("id") int id) {
		this.productService.deleteProduct(id);
		return "redirect:/admin/products";
	}

	@GetMapping("customers")
	public ModelAndView getCustomerDetail() {
		ModelAndView mView = new ModelAndView("displayCustomers");
		List<User> users = this.userService.getUsers();
		mView.addObject("customers", users);
		return mView;
	}

	@GetMapping("profileDisplay")
	public String profileDisplay(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
			 PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE username = ?;")) {

			stmt.setString(1, username);
			ResultSet rst = stmt.executeQuery();

			if (rst.next()) {
				model.addAttribute("userid", rst.getInt(1));
				model.addAttribute("username", rst.getString(2));
				model.addAttribute("email", rst.getString(3));
				model.addAttribute("password", rst.getString(4));
				model.addAttribute("address", rst.getString(5));
			}
		} catch (Exception e) {
			logger.error("Exception: {}", e.getMessage(), e);
		}
		return "updateProfile";
	}

	@PostMapping("updateuser")
	public String updateUserProfile(@RequestParam("userid") int userid,
									@RequestParam("username") String username,
									@RequestParam("email") String email,
									@RequestParam("password") String password,
									@RequestParam("address") String address) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
			 PreparedStatement pst = con.prepareStatement("UPDATE users SET username=?, email=?, password=?, address=? WHERE uid=?;")) {

			pst.setString(1, username);
			pst.setString(2, email);
			pst.setString(3, password);
			pst.setString(4, address);
			pst.setInt(5, userid);
			pst.executeUpdate();

			Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
					username,
					password,
					SecurityContextHolder.getContext().getAuthentication().getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		} catch (Exception e) {
			logger.error("Exception: {}", e.getMessage(), e);
		}
		return "redirect:index";
	}
}

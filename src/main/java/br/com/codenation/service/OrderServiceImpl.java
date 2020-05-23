package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> itens) {
		double valorNoSale = getValueBySale(itens, false);
		double valorYesSale = getValueBySale(itens, true)*0.8;

		return valorNoSale + valorYesSale;
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		return ids.stream()
				.map(productRepository::findById)
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		return orders.stream().mapToDouble(l->calculateOrderValue(l)).sum();
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {

		return productIds.stream()
						 .map(productRepository::findById)
						 .filter(Optional::isPresent)
						 .map(Optional::get)
						 .collect(Collectors.groupingBy(p->p.getIsSale()));

	}



	private double getValueBySale(List<OrderItem> itens, boolean sale){
		return filterOrderItemBySale(itens, sale)
				.stream()
				.mapToDouble(p->productRepository.findById(p.getProductId()).get().getValue()*p.getQuantity()).sum();
	}

	private List<OrderItem> filterOrderItemBySale(List<OrderItem> itens, boolean sale){
		return itens.stream().filter(p->productRepository.findById(p.getProductId()).get().getIsSale().equals(sale)).collect(Collectors.toList());
	}

}
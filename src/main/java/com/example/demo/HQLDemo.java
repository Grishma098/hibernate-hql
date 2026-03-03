package com.example.demo;

import com.example.entity.Product;
import com.example.loader.ProductDataLoader;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class HQLDemo {

    public static void main(String[] args) {

        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        // Load sample data (run once)
        ProductDataLoader.loadSampleProducts(session);

        sortProductsByPriceAscending(session);
        sortProductsByPriceDescending(session);
        sortProductsByQuantityDescending(session);

        getFirstThreeProducts(session);
        getNextThreeProducts(session);

        countTotalProducts(session);
        countProductsInStock(session);
        findMinMaxPrice(session);

        filterProductsByPriceRange(session, 20, 100);

        session.close();
        factory.close();
    }

    // Sorting
    public static void sortProductsByPriceAscending(Session session) {
        String hql = "FROM Product p ORDER BY p.price ASC";
        List<Product> products = session.createQuery(hql, Product.class).list();

        System.out.println("\n=== Ascending Price ===");
        products.forEach(System.out::println);
    }

    public static void sortProductsByPriceDescending(Session session) {
        String hql = "FROM Product p ORDER BY p.price DESC";
        List<Product> products = session.createQuery(hql, Product.class).list();

        System.out.println("\n=== Descending Price ===");
        products.forEach(System.out::println);
    }

    public static void sortProductsByQuantityDescending(Session session) {
        String hql = "FROM Product p ORDER BY p.quantity DESC";
        List<Product> products = session.createQuery(hql, Product.class).list();

        System.out.println("\n=== Quantity Desc ===");
        products.forEach(p -> System.out.println(p.getName() + " - " + p.getQuantity()));
    }

    // Pagination
    public static void getFirstThreeProducts(Session session) {
        Query<Product> query = session.createQuery("FROM Product", Product.class);
        query.setFirstResult(0);
        query.setMaxResults(3);

        System.out.println("\n=== First 3 ===");
        query.list().forEach(System.out::println);
    }

    public static void getNextThreeProducts(Session session) {
        Query<Product> query = session.createQuery("FROM Product", Product.class);
        query.setFirstResult(3);
        query.setMaxResults(3);

        System.out.println("\n=== Next 3 ===");
        query.list().forEach(System.out::println);
    }

    // Aggregates
    public static void countTotalProducts(Session session) {
        Long count = session.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                            .uniqueResult();

        System.out.println("\nTotal Products: " + count);
    }

    public static void countProductsInStock(Session session) {
        Long count = session.createQuery("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0", Long.class)
                            .uniqueResult();

        System.out.println("Products in Stock: " + count);
    }

    public static void findMinMaxPrice(Session session) {
        Object[] result = session.createQuery("SELECT MIN(p.price), MAX(p.price) FROM Product p", Object[].class)
                                 .uniqueResult();

        System.out.println("Min Price: " + result[0]);
        System.out.println("Max Price: " + result[1]);
    }

    // Filter
    public static void filterProductsByPriceRange(Session session, double min, double max) {
        List<Product> products = session.createQuery(
                "FROM Product p WHERE p.price BETWEEN :min AND :max", Product.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .list();

        System.out.println("\n=== Price Range ===");
        products.forEach(p -> System.out.println(p.getName() + " - " + p.getPrice()));
    }
}
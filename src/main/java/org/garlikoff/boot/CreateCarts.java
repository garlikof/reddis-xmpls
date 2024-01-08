package org.garlikoff.boot;

import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Book;
import org.garlikoff.model.Cart;
import org.garlikoff.model.CartItem;
import org.garlikoff.model.User;
import org.garlikoff.repository.BookRepository;
import org.garlikoff.repository.CartRepository;
import org.garlikoff.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

@Component
@Order(5)
@Slf4j
public class CreateCarts implements CommandLineRunner {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    RedisTemplate<String, String> template;
    @Autowired
    CartService cartService;

    @Value("${app.numberOfCarts}")
    private Integer numberOfCarts;
    @Override
    public void run(String... args) throws Exception {
         if (cartRepository.count() == 0){
             Random random = new Random();
             IntStream.range(0, numberOfCarts).forEach(n ->{
                String userId = template.opsForSet().randomMember(User.class.getName());
                Cart cart = Cart.builder().userId(userId).build();

                Set<Book> books = getRandomBooks(bookRepository, 7);
                cart.setCartItems(getCartItemsForBook(books));
                cartRepository.save(cart);

                if (random.nextBoolean())
                    cartService.checkout(cart.getId());
             });
             log.info(">>> created carts");
        }

    }

    private Set<Book> getRandomBooks(BookRepository bookRepository, int max){
        Random random = new Random();
        int howMany = random.nextInt(max) + 1;
        Set<Book> books = new HashSet<>(howMany);
        IntStream.range(0, howMany).forEach(n->{
            String randomBookId = template.opsForSet().randomMember(Book.class.getName());
            books.add(bookRepository.findById(randomBookId).get());
        });
        return books;

    }
    private Set<CartItem> getCartItemsForBook(Set<Book> books){
        Set<CartItem> items = new HashSet<CartItem>();
        books.forEach(book ->{
            CartItem item = CartItem.builder()
                    .isbn(book.getId())
                    .price(book.getPrice())
                    .quantity(1l)
                    .build();
            items.add(item);
        });
        return items;
    }
}

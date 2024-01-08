package org.garlikoff.boot;

import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Book;
import org.garlikoff.model.BookRating;
import org.garlikoff.model.User;
import org.garlikoff.repository.BookRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@Slf4j
@Order(4)
public class CreateBookRating implements CommandLineRunner {

    @Value("${app.numberOfRatings}")
    private Long numberOfRatings;
    @Value("${app.ratingStars}")
    private Integer ratingStars;

    @Autowired
    RedisTemplate<String, String> template;
    @Autowired
    BookRatingRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() < numberOfRatings ) {
            Random random = new Random();
            Long cnt = repository.count();
            IntStream.range(0, (int)(numberOfRatings - cnt)).forEach(n ->{
                String bookId = template.opsForSet().randomMember(Book.class.getName());
                String userId = template.opsForSet().randomMember(User.class.getName());

                int stars = random.nextInt(ratingStars) + 1;
                User user = new User();
                user.setId(userId);

                Book book = new Book();
                book.setId(bookId);

                BookRating rating = BookRating.builder().book(book)
                        .user(user)
                        .rating(stars)
                        .build();
                repository.save(rating);
            });
            log.info("rating created");
        }

    }
}

package org.garlikoff.boot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Book;
import org.garlikoff.model.Category;
import org.garlikoff.repository.BookRepository;
import org.garlikoff.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@Order(3)
public class CreateBooks implements CommandLineRunner {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0){
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Book>> typeReference = new TypeReference<List<Book>>(){
            };
            List<File> files =
                    Files.list(Paths.get(getClass().getResource("/data/books").toURI()))
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".json"))
                            .map(java.nio.file.Path::toFile)
                            .collect(Collectors.toList());
            Map<String, Category> categories = new HashMap<String, Category>();
            files.forEach(file ->{
                try {
                    log.info(">>>process book file " + file.getPath());
                    String categoryName = file.getName().substring(0, file.getName().lastIndexOf("_"));
                    log.info("category:{}", categoryName);

                    Category category;
                    if (!categories.containsKey(categoryName)){
                        category = Category.builder().name(categoryName).build();
                        categoryRepository.save(category);
                        categories.put(categoryName, category);
                    } else {
                        category = categories.get(categoryName);
                    }
                    InputStream inputStream = new FileInputStream(file);
                    List<Book> books = mapper.readValue(inputStream, typeReference);
                    books.stream().forEach((book) ->{
                        book.addCategory(category);
                        bookRepository.save(book);
                    });
                    log.info(">>>" + books.size() + " saved");

                } catch (Exception e){
                    log.error("unable import books: " + e);
                    e.printStackTrace();

                }
            });
            log.info(">>> Loaded books");
        }

    }
}

package org.garlikoff.controller;

import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Book;
import org.garlikoff.model.Category;
import org.garlikoff.repository.BookRepository;
import org.garlikoff.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {
    @Value("${app.booksSearchIndexName}")
    private String searchIndexName;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private StatefulRediSearchConnection<String, String> searchConnection;

    //    @GetMapping
//    public Iterable<Book> getAll(){
//        return bookRepository.findAll();
//    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pagedResult = bookRepository.findAll(paging);
        List<Book> books = pagedResult.hasContent() ? pagedResult.getContent() : Collections.emptyList();

        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("page", pagedResult.getNumber());
        response.put("pages", pagedResult.getTotalPages());
        response.put("total", pagedResult.getTotalElements());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public Iterable<Category> getAllCategotries(){
        return categoryRepository.findAll();
    }

    @GetMapping("/{isbn}")
    public Book getByIsbn(@PathVariable("isbn") String isbn){
        Optional<Book> book = bookRepository.findById(isbn);
        if (book.isPresent())
            return book.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "isbn not found");
    }

    //@Cacheable
    @GetMapping("/search")
    @Cacheable("book-search")
    public SearchResults<String, String> search(@RequestParam(name = "q") String query){
        log.info("get search request whith query={}", query);
        RediSearchCommands<String, String> commands = searchConnection.sync();
        SearchResults<String, String> results = commands.search(searchIndexName, query);
        return results;
    }

}

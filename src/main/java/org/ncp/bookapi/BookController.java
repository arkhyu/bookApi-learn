package org.ncp.bookapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @GetMapping("/book")
    public String GetBook()
    {
        return "The library is closed!";
    }
}

package me.summerbell.jpashop.controller;

import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.item.Book;
import me.summerbell.jpashop.domain.item.Item;
import me.summerbell.jpashop.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model){
        Book findItem = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(findItem.getId());
        form.setAuthor(findItem.getAuthor());
        form.setIsbn(findItem.getIsbn());
        form.setName(findItem.getName());
        form.setPrice(findItem.getPrice());
        form.setStockQuantity(findItem.getStockQuantity());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){

        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        // itemService.saveItem(book);
        itemService.updateBook(book);
        return "redirect:/items";

    }

}

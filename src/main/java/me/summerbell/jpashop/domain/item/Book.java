package me.summerbell.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter @Setter
public class Book extends Item{

    private String author;

    private String isbn;


    public void change(Book book){
        ((Item) book).change(book);
        this.author = book.author;
        this.isbn = book.isbn;
    }


}

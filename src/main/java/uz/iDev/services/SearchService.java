package uz.iDev.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.utilsInlineBoard;
import uz.iDev.config.LangConfig;
import uz.iDev.entity.Book;
import uz.iDev.repository.bookRepository.BookRepository;

import java.util.List;


import static uz.iDev.FreePdfBot.SearchStatus;
import static uz.iDev.handlers.CallbackHandler.count;
import static uz.iDev.handlers.CallbackHandler.messages;
import static uz.iDev.handlers.MessageHandler.bookMap;
import static uz.iDev.handlers.MessageHandler.isSearching;

/**
 * @author Narzullayev Husan, вс 10:08. 02.01.2022
 */

public class SearchService {
private static final SearchService instance = new SearchService();
private static final FreePdfBot BOT = FreePdfBot.getInstance();
    private static final OtherService otherService = OtherService.getInstance();

    public void search(String chatId, String message) {
        List<Book> books = BookRepository.get(message, 0);
        if(books.size() == 0){
            SendMessage sendMessage =new SendMessage(chatId, LangConfig.get(chatId,"book.not.found"));
            SearchStatus.put(chatId,"0");
            BOT.executeMessage(sendMessage);
            isSearching = false;
            otherService.menuExecte(chatId);
            return;
        }

        count.put(chatId,0);
        bookMap.put(chatId,books);
        messages.put(chatId,message);

        utilsInlineBoard.next(books,chatId);

        isSearching = false;

    }


    public static SearchService getInstance() {
        return instance;
    }
}
